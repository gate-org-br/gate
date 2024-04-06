package gate.io;

import gate.entity.User;
import gate.error.InvalidCredentialsException;
import gate.type.ID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

public class Token
{

	private static final SecretKey SECRET = Jwts.SIG.HS256.key().build();

	private Token()
	{

	}

	public static User parse(String string) throws InvalidCredentialsException
	{
		try
		{
			Claims claims = Jwts.parser()
				.verifyWith(SECRET)
				.build()
				.parseSignedClaims(string)
				.getPayload();

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
			.expiration(Date.from(Instant.now().plusSeconds(600)))
			.signWith(SECRET).compact();

		return credentials;
	}
}
