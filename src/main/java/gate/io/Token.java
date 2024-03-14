package gate.io;

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

public class Token
{

	private static final Key SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private Token()
	{

	}

	public static User parse(String string) throws InvalidCredentialsException
	{
		try
		{
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(SECRET)
				.build()
				.parseClaimsJws(string)
				.getBody();

			return new User().setId(ID.valueOf(claims.get("id", String.class)));
		} catch (RuntimeException ex)
		{
			throw new InvalidCredentialsException(ex.getMessage());
		}
	}

	public static String create(User user)
	{
		String credentials = Jwts.builder()
			.claim("id", user.getId().toString())
			.setExpiration(Date.from(Instant.now().plusSeconds(600)))
			.signWith(SECRET).compact();

		return credentials;
	}
}
