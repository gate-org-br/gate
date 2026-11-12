package gate.security;

import gate.error.ConversionException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

	public Claims aud(String value) {return claim("aud", value);}

	/**
	 * jjwt returns the aud claim as a set when parsing a token, while it may be
	 * a plain string when the token is being built.
	 */
	public Set<String> aud()
	{
		Object aud = get("aud");
		if (aud instanceof String string)
			return Set.of(string);
		if (aud instanceof Collection<?> collection)
			return collection.stream().map(String::valueOf).collect(Collectors.toUnmodifiableSet());
		return Set.of();
	}

	/**
	 * Reads the aud claim of a token <b>without verifying its signature</b>.
	 * Use it only to decide who should handle the token, never to authenticate.
	 *
	 * @return the audiences of the token, or an empty set if the token has no
	 * aud claim or is not a JWT
	 */
	public static Set<String> audiences(String token)
	{
		String[] parts = token == null ? new String[0] : token.split("\\.");
		if (parts.length != 3)
			return Set.of();

		try
		{
			var payload = JsonObject.parse(new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8));
			var aud = payload.getJsonElement("aud").orElse(null);
			if (aud instanceof JsonString string)
				return Set.of(string.getValue());
			if (aud instanceof JsonArray array)
				return array.stream()
						.filter(JsonString.class::isInstance)
						.map(e -> ((JsonString) e).getValue())
						.collect(Collectors.toUnmodifiableSet());
			return Set.of();
		} catch (IllegalArgumentException | ConversionException ex)
		{
			return Set.of();
		}
	}

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