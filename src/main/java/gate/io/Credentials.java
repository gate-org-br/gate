package gate.io;

import gate.entity.Auth;
import gate.entity.User;
import gate.error.ConversionException;
import gate.error.InvalidCredentialsException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.stream.UncheckedOptional;
import gate.type.ID;
import gate.util.SystemProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

public class Credentials
{

	private static final SecretKey SECRET = UncheckedOptional.of(SystemProperty.get("gate.key-store.file"))
			.map(filename ->
			{
				String key = SystemProperty.get("gate.key-store.secret-key").orElse("secret-key");
				char[] password = SystemProperty.get("gate.key-store.password").orElse("changeit").toCharArray();

				KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
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

	private Credentials()
	{

	}

	public static User of(String string) throws InvalidCredentialsException
	{
		try
		{
			Claims claims = Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(string)
					.getPayload();

			User user = new User();
			user.setId(ID.valueOf(claims.get("id", String.class)));
			user.setName(claims.get("name", String.class));
			user.getRole().setId(ID.valueOf(claims.get("role.id", String.class)));
			user.getRole().setName(claims.get("role.name", String.class));

			user.setAuths(JsonArray.parse(claims.get("auths", String.class)).stream()
					.map(e -> (JsonObject) e)
					.map(e -> new Auth()
					.setAccess(e.getString("access").map(Auth.Access::valueOf).orElseThrow())
					.setScope(e.getString("scope").map(Auth.Scope::valueOf).orElseThrow())
					.setModule(e.getString("module").orElse(null))
					.setScreen(e.getString("screen").orElse(null))
					.setAction(e.getString("action").orElse(null))).toList()
			);

			return user;
		} catch (RuntimeException | ConversionException ex)
		{
			throw new InvalidCredentialsException(ex.getMessage());
		}
	}

	public static String create(User user)
	{
		String credentials = Jwts.builder()
				.claim("id", user.getId().toString())
				.claim("name", user.getName())
				.claim("role.id", user.getRole().getId().toString())
				.claim("role.name", user.getRole().getName())
				.claim("auths", user.getComputedAuths()
						.stream()
						.map(e -> new JsonObject()
						.setString("access", e.getAccess().name())
						.setString("scope", e.getScope().name())
						.setString("module", e.getModule())
						.setString("screen", e.getScreen())
						.setString("action", e.getAction()))
						.collect(Collectors.toCollection(JsonArray::new))
						.toString())
				.expiration(Date.from(Instant.now().plusSeconds(3600)))
				.signWith(SECRET).compact();

		return credentials;
	}
}
