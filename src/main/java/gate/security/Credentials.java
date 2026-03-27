package gate.security;

import gate.cache.Cache;
import gate.converter.Converter;
import gate.error.HierarchyException;
import gate.error.InvalidUsernamePasswordException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.type.ID;
import gate.util.SystemProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class Credentials
{

	private final SecretKey secret;
	private static final Cache<Duration> IDLE_TIMEOUT = Cache.builder(()
					-> SystemProperty.get("gate.auth.session.idle_timeout")
					.map(e -> Converter.getConverter(Duration.class)
							.ofString(Duration.class, e))
					.map(e -> (Duration) e)
					.orElse(Duration.ofHours(1)))
			.build();

	private static final Cache<Duration> TIMEOUT = Cache.builder(()
					-> SystemProperty.get("gate.auth.session.timeout")
					.map(e -> Converter.getConverter(Duration.class)
							.ofString(Duration.class, e))
					.map(e -> (Duration) e)
					.orElse(Duration.ofHours(24)))
			.build();

	@Inject
	public Credentials(CryptoKeys cryptoKeys)
	{
		this.secret = cryptoKeys.jwtSigningKey();
	}

	public String create(JsonObject claims)
	{
		return Jwts.builder()
				.claims(claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())))
				.issuedAt(Date.from(Instant.now()))
				.expiration(Date.from(Instant.now().plus(IDLE_TIMEOUT.get())))
				.signWith(secret)
				.compact();
	}

	public JsonObject parse(String token) throws UnauthorizedException
	{
		return getPayload(token)
				.entrySet()
				.stream()
				.collect(JsonObject::new, (c, e) -> c.put(e.getKey(),
								JsonElement.of(e.getValue())),
						JsonObject::putAll);
	}

	public String fromToken(SubjectToken subject)
	{
		return Jwts.builder()
				.subject(subject.id.toString())
				.issuedAt(Date.from(subject.iat.toInstant(ZoneOffset.UTC)))
				.expiration(Date.from(subject.exp.toInstant(ZoneOffset.UTC)))
				.signWith(secret)
				.compact();
	}

	public SubjectToken toToken(String token)
			throws InvalidUsernamePasswordException,
				   HierarchyException, UnauthorizedException
	{
		var payload = getPayload(token);

		var iat = LocalDateTime.ofInstant(payload.getIssuedAt().toInstant(), ZoneOffset.UTC);
		var exp = LocalDateTime.ofInstant(payload.getExpiration().toInstant(), ZoneOffset.UTC);

		if (iat.plus(TIMEOUT.get()).isBefore(LocalDateTime.now(ZoneOffset.UTC)))
			throw new UnauthorizedException("Attempt to authenticate with expired token");

		return new SubjectToken(iat, exp, ID.valueOf(payload.getSubject()));
	}

	private Claims getPayload(String token)
	{
		try
		{
			return Jwts.parser()
					.verifyWith(secret)
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

	public String refresh(String token)
	{
		return fromToken(toToken(token).refresh());
	}

	public record SubjectToken(LocalDateTime iat, LocalDateTime exp, ID id)
	{

		public SubjectToken refresh()
		{
			return new SubjectToken(iat, LocalDateTime.now(ZoneOffset.UTC)
					.plusSeconds(IDLE_TIMEOUT.get().getSeconds()), id);
		}

		public static SubjectToken create(ID id)
		{
			var iat = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
			return new SubjectToken(iat, iat.plusSeconds(IDLE_TIMEOUT.get().getSeconds()), id);
		}
	}

}