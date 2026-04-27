package gate.security;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.type.ID;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Credentials
{
	private static final SessionPolicy POLICY = SessionPolicy.CURRENT;

	private final LocalDateTime iat;
	private final LocalDateTime exp;
	private final ID sub;
	private final ID sid;
	private final User usr;

	private Credentials(LocalDateTime iat, LocalDateTime exp, ID sub, ID sid, User usr)
	{
		if (iat == null
		    || exp == null
		    || sub == null
		    || usr != null && (usr.getId() == null || !sub.equals(usr.getId())))
			throw new UnauthorizedException("Attempt to create credentials with invalid session");

		this.iat = iat;
		this.exp = exp;
		this.sub = sub;
		this.sid = sid;
		this.usr = usr;
	}


	public ID sub() {return sub;}

	public boolean revocable() {return sid != null;}

	public boolean stateless() {return usr != null;}

	public ID sid() {return sid;}

	public User usr() {return usr;}

	@Override public String toString()
	{
		return new Claims()
				.sub(sub.toString())
				.iat(iat.toInstant(ZoneOffset.UTC))
				.exp(exp.toInstant(ZoneOffset.UTC))
				.sid(sid != null ? sid.toString() : null)
				.claim("usr", usr != null
						? new JsonObject()
						  .setObject("id", usr.getId())
						  .setString("name", usr.getName())
						  .setString("username", usr.getUsername())
						  .setString("email", usr.getEmail())
						  .set("auths", usr.computedAuthStream()
										.map(e -> new JsonObject()
												  .setObject("id", e.getId())
												  .setString("module", e.getModule())
												  .setString("screen", e.getScreen())
												  .setString("action", e.getAction())
												  .setObject("scope", e.getScope())
												  .setObject("access", e.getAccess()))
										.collect(Collectors.toCollection(JsonArray::new)))
						  .set("role", usr.getRole().parentStream()
									   .map(e -> new JsonObject()
												 .setObject("id", e.getId())
												 .setString("name", e.getName())
												 .setString("rolename", e.getRolename())
												 .setObject("email", e.getEmail()))
									   .collect(Collectors.collectingAndThen(Collectors.toList(), nodes ->
									   {
										   for (int i = 0; i < nodes.size() - 1; i++)
											   nodes.get(i).set("role", nodes.get(i + 1));
										   return nodes.isEmpty() ? null : nodes.get(0);
									   }))).unwrap()
						: null).toString();
	}

	public Credentials refresh()
	{
		return new Credentials(iat, LocalDateTime.now(ZoneOffset.UTC)
				.truncatedTo(ChronoUnit.SECONDS)
				.plusSeconds(POLICY.idleTimeout().getSeconds()), sub, sid, usr);
	}

	public static Credentials create(ID sub, ID sid, User user)
	{
		var iat = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
		var exp = iat.plusSeconds(POLICY.idleTimeout().getSeconds());
		return new Credentials(iat, exp, sub, sid, user);
	}

	@SuppressWarnings("rawtypes") public static Credentials parse(String token)
			throws HierarchyException, UnauthorizedException
	{
		var claims = gate.security.Claims.valueOf(token);

		var iat = claims.iat().map(e -> LocalDateTime.ofInstant(e, ZoneOffset.UTC))
				.orElseThrow(() -> new UnauthorizedException("Attempt to authenticate with invalid token"));
		var exp = claims.exp().map(e -> LocalDateTime.ofInstant(e, ZoneOffset.UTC))
				.orElseThrow(() -> new UnauthorizedException("Attempt to authenticate with invalid token"));

		if (iat.plus(POLICY.timeout()).isBefore(LocalDateTime.now(ZoneOffset.UTC)))
			throw new UnauthorizedException("Attempt to authenticate with expired token");

		var sid = claims.sid().map(ID::valueOf).orElse(null);
		var sub = claims.sub().map(ID::valueOf)
				.orElseThrow(() -> new UnauthorizedException("Attempt to authenticate with invalid token"));

		var usr = claims.get("usr") instanceof Map map
				? new User()
				  .setId(ID.valueOf((String) map.get("id")))
				  .setName((String) map.get("name"))
				  .setUsername((String) map.get("username"))
				  .setEmail((String) map.get("email"))
				  .setAuths(map.containsKey("auths")
							? ((List<?>) map.get("auths"))
							  .stream()
							  .filter(e -> e instanceof Map)
							  .map(e -> (Map) e)
							  .map(e -> new Auth()
										.setId(e.get("id") instanceof String string ? ID.valueOf(string) : null)
										.setScope(e.get("scope") instanceof String string ? Auth.Scope.valueOf(string) : null)
										.setAccess(e.get("access") instanceof String string ? Auth.Access.valueOf(string) : null)
										.setModule((String) e.get("module"))
										.setScreen((String) e.get("screen"))
										.setAction((String) e.get("action")))
							  .toList()
							: null)
				  .setRole(Stream.iterate((Map) map.get("role"),
						  Objects::nonNull,
						  r -> (Map) r.get("role"))
						   .map(r -> new Role()
									 .setId(r.get("id") instanceof String string ? ID.valueOf(string) : null)
									 .setName((String) r.get("name"))
									 .setRolename((String) r.get("rolename"))
									 .setEmail((String) r.get("email")))
						   .collect(Collectors.collectingAndThen(Collectors.toList(), nodes ->
						   {
							   for (int i = 0; i < nodes.size() - 1; i++)
								   nodes.get(i).setRole(nodes.get(i + 1));
							   return nodes.isEmpty() ? null : nodes.get(0);
						   })))
				: null;

		return new Credentials(iat, exp, sub, sid, usr);
	}
}