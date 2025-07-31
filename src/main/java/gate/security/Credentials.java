package gate.security;

import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.type.ID;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

public class Credentials
{

	private final SecretKey secret;

	public Credentials(SecretKey secret)
	{
		this.secret = secret;
	}

	public String create(JsonObject claims)
	{
		return Jwts.builder()
			.claims(claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())))
			.expiration(Date.from(Instant.now().plusSeconds(3600)))
			.signWith(secret)
			.compact();
	}

	public JsonObject parse(String token) throws UnauthorizedException
	{
		try
		{
			return Jwts.parser()
				.verifyWith(secret)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.entrySet()
				.stream()
				.collect(JsonObject::new, (c, e) -> c.put(e.getKey(), JsonElement.of(e.getValue())), JsonObject::putAll);
		} catch (SignatureException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with invalid signature");
		} catch (ExpiredJwtException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with expired token");
		}
	}

	public String subject(User user)
	{
		return Jwts.builder()
			.subject(user.getId().toString())
			.expiration(Date.from(Instant.now().plusSeconds(3600)))
			.signWith(secret)
			.compact();
	}

	public ID subject(String token) throws InvalidUsernameException, HierarchyException, UnauthorizedException
	{
		try
		{
			return ID.valueOf(Jwts.parser()
				.verifyWith(secret)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("sub", String.class));
		} catch (SignatureException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with invalid signature");
		} catch (ExpiredJwtException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with expired token");
		}
	}
}
