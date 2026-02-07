package gate.security;

import gate.util.SystemProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped
public class CryptoKeys
{

	private final SecretKey signKey;
	private final SecretKey encryptionKey;

	public CryptoKeys()
	{
		SecretKey master = loadMasterKey();
		this.signKey = deriveHmac(master, "sign-key");
		this.encryptionKey = deriveAes(master, "encryption-key");
	}

	public SecretKey signKey()
	{
		return signKey;
	}

	public SecretKey encryptionKey()
	{
		return encryptionKey;
	}

	private static SecretKey loadMasterKey()
	{
		return loadFromKeyStore()
			.or(CryptoKeys::loadFromFile)
			.or(CryptoKeys::loadFromSystemProperty)
			.orElseGet(Jwts.SIG.HS256.key()::build);
	}

	private static Optional<SecretKey> loadFromSystemProperty()
	{
		return SystemProperty.get("gate.secret-key")
			.map(e -> Keys.hmacShaKeyFor(Base64.getDecoder().decode(e)));
	}

	private static Optional<SecretKey> loadFromKeyStore()
	{
		String keystoreFile = SystemProperty.get("gate.key-store.file").orElse(null);
		if (keystoreFile == null)
			return Optional.empty();
		try
		{
			String keyAlias
				= SystemProperty.get("gate.key-store.secret-key")
					.orElse("secret-key");

			char[] password
				= SystemProperty.get("gate.key-store.password")
					.orElse("changeit")
					.toCharArray();

			KeyStore keyStore = KeyStore.getInstance(
				keystoreFile.toLowerCase().endsWith(".p12") ? "PKCS12" : "JCEKS"
			);

			try (FileInputStream fis = new FileInputStream(keystoreFile))
			{
				keyStore.load(fis, password);
			}

			return Optional.of((SecretKey) keyStore.getKey(keyAlias, password));
		} catch (IOException
			| KeyStoreException
			| NoSuchAlgorithmException
			| UnrecoverableKeyException
			| CertificateException e)
		{
			throw new RuntimeException("Erro ao carregar keystore", e);
		}
	}

	private static Optional<SecretKey> loadFromFile()
	{
		String secretFile = SystemProperty.get("gate.secret-key-file").orElse(null);
		if (secretFile == null)
			return Optional.empty();
		try
		{
			byte[] bytes = Files.readAllBytes(Paths.get(secretFile));
			byte[] decoded = Base64.getDecoder().decode(bytes);
			return Optional.of(Keys.hmacShaKeyFor(decoded));
		} catch (WeakKeyException | IOException e)
		{
			throw new RuntimeException("Erro ao ler secret-key-file", e);
		}

	}

	private static byte[] derive(SecretKey master, String info)
	{
		try
		{
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(master);
			return mac.doFinal(info.getBytes(StandardCharsets.UTF_8));
		} catch (IllegalStateException
			| InvalidKeyException
			| NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static SecretKey deriveHmac(SecretKey master, String info)
	{
		return Keys.hmacShaKeyFor(derive(master, info));
	}

	private static SecretKey deriveAes(SecretKey master, String info)
	{
		return new SecretKeySpec(Arrays.copyOf(derive(master, info), 32), "AES");
	}

}
