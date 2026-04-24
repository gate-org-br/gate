package gate.security;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.type.ID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Credentials
{
	private static final SecretKey SECRET = CryptoKeys.JWS_KEY.get();
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
		return Jwts.builder()
				.subject(sub.toString())
				.issuedAt(Date.from(iat.toInstant(ZoneOffset.UTC)))
				.expiration(Date.from(exp.toInstant(ZoneOffset.UTC)))
				.claim("sid", sid != null ? sid.toString() : null)
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
										   return nodes.get(0);
									   }))).unwrap()
						: null)
				.signWith(SECRET)
				.compact();
	}

	public Credentials refresh()
	{
		return new Credentials(iat, LocalDateTime.now(ZoneOffset.UTC)
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
		var claims = getClaims(token);
		var iat = LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneOffset.UTC);
		var exp = LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneOffset.UTC);

		if (iat.plus(POLICY.timeout()).isBefore(LocalDateTime.now(ZoneOffset.UTC)))
			throw new UnauthorizedException("Attempt to authenticate with expired token");

		var sub = ID.valueOf(claims.getSubject());
		var sid = claims.get("sid") instanceof String string ? ID.valueOf(string) : null;

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

	private static Claims getClaims(String token)
	{
		try
		{
			return Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (SignatureException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with invalid signature");
		} catch (ExpiredJwtException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with expired token");
		} catch (JwtException | IllegalArgumentException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with invalid token");
		}
	}
}
