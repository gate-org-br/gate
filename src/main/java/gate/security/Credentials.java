package gate.security;

import gate.converter.Converter;
import gate.error.UnauthorizedException;
import gate.type.ID;
import gate.util.SystemProperty;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@ApplicationScoped
public class Credentials
{
	private final CryptoKeys cryptoKeys;

	public static final Duration TIMEOUT = SystemProperty.get("gate.auth.session.timeout")
			.or(() -> Optional.of("7d"))
			.map(e -> Converter.fromString(Duration.class, e))
			.orElseThrow(() -> new IllegalArgumentException("Property gate.auth.session.timeout must be a valid duration"));

	public static final Duration IDLE_TIMEOUT = SystemProperty.get("gate.auth.session.idle_timeout")
			.or(() -> Optional.of("1h"))
			.map(e -> Converter.fromString(Duration.class, e))
			.orElseThrow(() -> new IllegalArgumentException("Property gate.auth.session.idle_timeout must be a valid duration"));


	@Inject
	public Credentials(CryptoKeys cryptoKeys)
	{
		this.cryptoKeys = cryptoKeys;
	}

	public String createToken(Subject subject)
	{
		return Jwts.builder()
				.subject(subject.id().toString())
				.issuedAt(Date.from(subject.issuedAt()))
				.expiration(Date.from(Instant.now().plusSeconds(IDLE_TIMEOUT.toSeconds())))
				.signWith(this.cryptoKeys.signKey())
				.compact();
	}

	public Subject parseToken(String token) throws UnauthorizedException
	{
		try
		{
			var claims = Jwts.parser()
					.verifyWith(this.cryptoKeys.signKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();

			if (claims.getIssuedAt().getTime() + TIMEOUT.toMillis() < System.currentTimeMillis())
				throw new UnauthorizedException("Attempt to authenticate with expired token");

			return new Subject(ID.valueOf(claims.getSubject()),
					claims.getIssuedAt().toInstant());
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
		return createToken(parseToken(token));
	}

	public record Subject(ID id, Instant issuedAt) {}
}