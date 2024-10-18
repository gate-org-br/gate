package gate.io;

import gate.GateControl;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.error.UnauthorizedException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.stream.UncheckedOptional;
import gate.type.ID;
import gate.util.SystemProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@ApplicationScoped
public class Credentials
{

	@Inject
	GateControl control;

	private static final SecretKey SECRET = getSecret();

	private Credentials()
	{

	}

	public String create(JsonObject claims)
	{
		return Jwts.builder()
				.claims(claims.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toString())))
				.expiration(Date.from(Instant.now().plusSeconds(3600)))
				.signWith(SECRET)
				.compact();
	}

	public JsonObject parse(String token) throws UnauthorizedException
	{
		try
		{
			return Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.entrySet()
					.stream()
					.collect(JsonObject::new, (c, e) -> c.put(e.getKey(), JsonElement.of(e.getValue())), JsonObject::putAll);
		} catch (SignatureException ex)
		{
			return null;
		}
	}

	public String subject(User user)
	{
		return Jwts.builder()
				.subject(user.getId().toString())
				.expiration(Date.from(Instant.now().plusSeconds(3600)))
				.signWith(SECRET)
				.compact();
	}

	public User subject(String token) throws InvalidUsernameException, HierarchyException, UnauthorizedException
	{
		try
		{
			return control.select(ID.valueOf(Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.get("sub", String.class)));
		} catch (SignatureException ex)
		{
			return null;
		}
	}

	private static SecretKey getSecret()
	{
		return UncheckedOptional.of(SystemProperty.get("gate.key-store.file"))
				.map(filename ->
				{
					String key = SystemProperty.get("gate.key-store.secret-key").orElse("secret-key");
					char[] password = SystemProperty.get("gate.key-store.password").orElse("changeit").toCharArray();

					KeyStore keyStore = KeyStore
							.getInstance(filename.toLowerCase().endsWith(".p12")
									? "PKCS12" : "JCEKS");
					try (FileInputStream fis = new FileInputStream(filename))
					{
						keyStore.load(fis, password);
					}
					return (SecretKey) keyStore.getKey(key, password);
				})
				.orElseGet(()
						-> UncheckedOptional.of(SystemProperty.get("gate.secret-key-file"))
						.map(Paths::get)
						.map(Files::readAllBytes)
						.map(Base64.getDecoder()::decode)
						.map(Keys::hmacShaKeyFor)
						.orElseGet(()
								-> SystemProperty.get("gate.secret-key")
								.map(Base64.getDecoder()::decode)
								.map(Keys::hmacShaKeyFor)
								.orElseGet(Jwts.SIG.HS256.key()::build)));
	}
}
