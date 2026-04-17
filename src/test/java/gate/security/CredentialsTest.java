package gate.security;

import gate.error.UnauthorizedException;
import gate.type.ID;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CredentialsTest
{

	@Test
	public void shouldCreateAndParseToken()
	{
		Credentials credentials = new Credentials(new CryptoKeys());
		Credentials.Subject subject = new Credentials.Subject(ID.valueOf(123), Instant.now());

		String token = credentials.createToken(subject);
		Credentials.Subject parsed = credentials.parseToken(token);

		assertEquals(subject.id(), parsed.id());
		assertEquals(subject.issuedAt().getEpochSecond(), parsed.issuedAt().getEpochSecond());
	}

	@Test
	public void shouldPreserveIssuedAtOnRefresh()
	{
		Credentials credentials = new Credentials(new CryptoKeys());
		Credentials.Subject subject = new Credentials.Subject(ID.valueOf(123), Instant.now().minusSeconds(120));

		String token = credentials.createToken(subject);
		Credentials.Subject refreshed = credentials.parseToken(credentials.refresh(token));

		assertEquals(subject.id(), refreshed.id());
		assertEquals(subject.issuedAt().getEpochSecond(), refreshed.issuedAt().getEpochSecond());
	}

	@Test
	public void shouldRejectTokenExpiredByAbsoluteTimeout()
	{
		CryptoKeys keys = new CryptoKeys();
		Credentials credentials = new Credentials(keys);
		String token = Jwts.builder()
				.subject(ID.valueOf(123).toString())
				.issuedAt(Date.from(Instant.now().minus(Credentials.TIMEOUT).minusSeconds(1)))
				.expiration(Date.from(Instant.now().plusSeconds(300)))
				.signWith(keys.signKey())
				.compact();

		assertThrows(UnauthorizedException.class, () -> credentials.parseToken(token));
	}

	@Test
	public void shouldRejectTokenSignedWithAnotherKey()
	{
		Credentials credentials = new Credentials(new CryptoKeys());
		SecretKey otherKey = Keys.hmacShaKeyFor(new byte[32]);
		String token = Jwts.builder()
				.subject(ID.valueOf(123).toString())
				.issuedAt(Date.from(Instant.now()))
				.expiration(Date.from(Instant.now().plusSeconds(300)))
				.signWith(otherKey)
				.compact();

		assertThrows(UnauthorizedException.class, () -> credentials.parseToken(token));
	}
}