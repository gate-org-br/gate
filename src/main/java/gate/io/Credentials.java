package gate.io;

import gate.entity.Auth;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import gate.type.ID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

public class Credentials
{

	private static final Pattern BEARER = Pattern.compile("Bearer (.*)");
	private static final Key SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private Credentials()
	{

	}

	public static User of(String string) throws InvalidCredentialsException
	{
		try
		{
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(SECRET)
				.build()
				.parseClaimsJws(string)
				.getBody();

			User user = new User();
			user.setId(ID.valueOf(claims.get("id", String.class)));
			user.setName(claims.get("name", String.class));
			user.getRole().setId(ID.valueOf(claims.get("role.id", String.class)));
			user.getRole().setName(claims.get("role.name", String.class));

			for (String row : claims.get("auths", String.class).split(","))
			{
				String[] cols = row.split(":");
				Auth auth = new Auth();
				auth.setAccess(Auth.Access.valueOf(cols[0]));
				auth.setScope(Auth.Scope.valueOf(cols[1]));
				if (!cols[2].equals("*"))
					auth.setModule(cols[2]);
				if (!cols[3].equals("*"))
					auth.setScreen(cols[3]);
				if (!cols[4].equals("*"))
					auth.setAction(cols[4]);
				user.getAuths().add(auth);
			}

			return user;
		} catch (RuntimeException ex)
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
			.claim("auths", user.getComputedAuths().stream().map(e -> new StringJoiner(":")
			.add(e.getAccess().name())
			.add(e.getScope().name())
			.add(e.getModule() != null ? e.getModule() : "*")
			.add(e.getScreen() != null ? e.getScreen() : "*")
			.add(e.getAction() != null ? e.getAction() : "*").toString())
				.collect(Collectors.joining(",")))
			.setExpiration(Date.from(Instant.now().plusSeconds(3600)))
			.signWith(SECRET).compact();

		return credentials;
	}

	public static boolean isPresent(HttpServletRequest request)
	{
		String header = request.getHeader("Authorization");
		return header != null && BEARER.matcher(header).matches();
	}

	public static Optional<User> of(HttpServletRequest request)
		throws InvalidCredentialsException
	{
		String header = request.getHeader("Authorization");
		if (header != null)
		{
			Matcher matcher = BEARER.matcher(header);
			if (matcher.matches())
				return Optional.of(of(matcher.group(1)));
			return Optional.empty();
		}

		return Optional.empty();
	}
}
