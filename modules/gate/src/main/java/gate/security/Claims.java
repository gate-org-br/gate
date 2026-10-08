package gate.security;

import gate.error.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Claims extends HashMap<String, Object>
{
	private static final SecretKey SECRET
			= CryptoKeys.JWS_KEY.get();

	public Claims()
	{
	}

	public Claims(Map<? extends String, ?> m)
	{
		super(m);
	}


	public Optional<Object> claim(String claim) {return Optional.ofNullable(get(claim));}

	public Claims claim(String key, Object value)
	{
		if (value == null)
			remove(key);
		else
			put(key, value);
		return this;
	}

	public Claims iss(String value) {return claim("iss", value);}

	public Optional<String> iss() {return claim("iss").map(String.class::cast);}

	public Optional<String> sid() {return claim("sid").map(String.class::cast);}

	public Claims sid(String value) {return claim("sid", value);}

	public Optional<String> sub() {return claim("sub").map(String.class::cast);}

	public Claims sub(String value) {return claim("sub", value);}

	public Optional<Instant> iat() {return claim("iat").map(Long.class::cast).map(Instant::ofEpochSecond);}

	public Claims iat(Instant value) {return claim("iat", value.getEpochSecond());}

	public Optional<Instant> exp() {return claim("exp").map(Long.class::cast).map(Instant::ofEpochSecond);}

	public Claims exp(Instant value) {return claim("exp", value.getEpochSecond());}

	public static Claims valueOf(String token)
	{
		try
		{
			return new Claims(Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(token)
					.getPayload());
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

	public String toString()
	{
		return Jwts.builder()
				.claims(this)
				.signWith(SECRET)
				.compact();
	}
}