package gate.producer;

import gate.security.Credentials;
import gate.stream.UncheckedOptional;
import gate.util.SystemProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Base64;
import javax.crypto.SecretKey;

@ApplicationScoped
public class CredentialsProducer
{

	private static final Credentials CREDENTIALS = new Credentials(UncheckedOptional.of(SystemProperty.get("gate.key-store.file"))
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
				.orElseGet(Jwts.SIG.HS256.key()::build))));

	@Produces
	@Dependent
	public Credentials produce()
	{
		return CREDENTIALS;
	}
}
