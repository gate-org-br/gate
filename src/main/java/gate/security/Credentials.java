package gate.security;

import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.type.ID;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
				.collect(JsonObject::new, (c, e) -> c.put(e.getKey(),
				JsonElement.of(e.getValue())),
					JsonObject::putAll);
		} catch (SignatureException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with invalid signature");
		} catch (ExpiredJwtException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with expired token");
		}
	}

	public String fromSubject(ID id)
	{
		final var now = Instant.now();
		return Jwts.builder()
			.subject(id.toString())
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(3600)))
			.signWith(secret)
			.compact();
	}

	public Subject toSubject(String token)
		throws InvalidUsernameException,
		HierarchyException, UnauthorizedException
	{
		try
		{
			var payload = Jwts.parser()
				.verifyWith(secret)
				.build()
				.parseSignedClaims(token)
				.getPayload();

			var iat = payload.getIssuedAt().toInstant().atZone(ZoneOffset.UTC)
				.toLocalDateTime();

			return new Subject(iat, ID.valueOf(payload.getSubject()));
		} catch (SignatureException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with invalid signature");
		} catch (ExpiredJwtException ex)
		{
			throw new UnauthorizedException("Attempt to authenticate with expired token");
		}
	}

	public String refresh(String token)
	{
		return fromSubject(toSubject(token).id());
	}

	public record Subject(LocalDateTime iat, ID id)
		{

	}

}
