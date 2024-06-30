package gate.io;

import gate.entity.Auth;
import gate.entity.User;
import gate.error.ConversionException;
import gate.error.InvalidCredentialsException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.type.ID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

public class Credentials
{

	private static final Pattern BEARER = Pattern.compile("Bearer (.*)");
	private static final SecretKey SECRET = Jwts.SIG.HS256.key().build();

	private Credentials()
	{

	}

	public static User of(String string) throws InvalidCredentialsException
	{
		try
		{
			Claims claims = Jwts.parser()
				.verifyWith(SECRET)
				.build()
				.parseSignedClaims(string)
				.getPayload();

			User user = new User();
			user.setId(ID.valueOf(claims.get("id", String.class)));
			user.setName(claims.get("name", String.class));
			user.getRole().setId(ID.valueOf(claims.get("role.id", String.class)));
			user.getRole().setName(claims.get("role.name", String.class));

			user.setAuths(JsonArray.parse(claims.get("auths", String.class)).stream()
				.map(e -> (JsonObject) e)
				.map(e -> new Auth()
				.setAccess(e.getString("access").map(Auth.Access::valueOf).orElseThrow())
				.setScope(e.getString("scope").map(Auth.Scope::valueOf).orElseThrow())
				.setModule(e.getString("module").orElse(null))
				.setScreen(e.getString("screen").orElse(null))
				.setAction(e.getString("action").orElse(null))).toList()
			);

			return user;
		} catch (RuntimeException | ConversionException ex)
		{
			throw new InvalidCredentialsException(ex.getMessage());
		}
	}

	public static String create(User user)
	{
		String credentials = Jwts.builder()
			.claim("id", user.getId().toString())
			.claim("name", user.getName())
			.claim("role.id", user.getRole().getId().toString())
			.claim("role.name", user.getRole().getName())
			.claim("auths", user.getComputedAuths()
				.stream()
				.map(e -> new JsonObject()
				.setString("access", e.getAccess().name())
				.setString("scope", e.getScope().name())
				.setString("module", e.getModule())
				.setString("screen", e.getScreen())
				.setString("action", e.getAction()))
				.collect(Collectors.toCollection(JsonArray::new))
				.toString())
			.expiration(Date.from(Instant.now().plusSeconds(3600)))
			.signWith(SECRET).compact();

		return credentials;
	}

	public static boolean isPresent(HttpServletRequest request)
	{
		String header = request.getHeader("Authorization");
		return header != null && BEARER.matcher(header).matches();
	}

	public static Optional<User> of(HttpServletRequest request) throws InvalidCredentialsException
	{
		String header = request.getHeader("Authorization");
		if (header == null)
			return Optional.empty();

		Matcher matcher = BEARER.matcher(header);
		if (!matcher.matches())
			return Optional.empty();

		return Optional.of(of(matcher.group(1)));
	}
}
