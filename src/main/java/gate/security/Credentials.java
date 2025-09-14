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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

public class Credentials
{

	private final SecretKey secret;
	public static final long EXP = 3600;

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

	public String fromToken(SubjectToken subject)
	{
		return Jwts.builder()
			.subject(subject.id.toString())
			.issuedAt(Date.from(subject.iat().atZone(ZoneOffset.UTC).toInstant()))
			.expiration(Date.from(subject.exp.atZone(ZoneOffset.UTC).toInstant()))
			.signWith(secret)
			.compact();
	}

	public SubjectToken toToken(String token)
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

			var exp = payload.getExpiration().toInstant().atZone(ZoneOffset.UTC)
				.toLocalDateTime();

			return new SubjectToken(iat, exp, ID.valueOf(payload.getSubject()));
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
		return fromToken(toToken(token).refresh());
	}

	public record SubjectToken(LocalDateTime iat, LocalDateTime exp, ID id)
		{

		public SubjectToken refresh()
		{
			return new SubjectToken(iat, LocalDateTime.now(ZoneOffset.UTC).plusSeconds(EXP), id);
		}

		public static SubjectToken create(ID id)
		{
			var iat = LocalDateTime.now(ZoneOffset.UTC)
				.truncatedTo(ChronoUnit.SECONDS);
			var exp = iat.plusSeconds(Credentials.EXP);
			return new SubjectToken(iat, exp, id);
		}

	}

}
