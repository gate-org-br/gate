package gate.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import gate.util.SystemProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class CryptoKeysProducer
{

	private static final CryptoKeys KEYS = load();

	private static CryptoKeys load()
	{

		SecretKey master = loadMasterKey();

		return new CryptoKeys(
			deriveHmac(master, "gate-jwt"),
			deriveAes(master, "gate-state")
		);
	}

	private static SecretKey loadMasterKey()
	{

		// 1) keystore
		String keystoreFile = SystemProperty.get("gate.key-store.file")
			.orElse(null);

		if (keystoreFile != null)
		{
			try
			{
				String keyAlias
					= SystemProperty.get("gate.key-store.secret-key")
						.orElse("secret-key");

				char[] password
					= SystemProperty.get("gate.key-store.password")
						.orElse("changeit")
						.toCharArray();

				KeyStore keyStore = KeyStore.getInstance(keystoreFile.toLowerCase().endsWith(".p12") ? "PKCS12" : "JCEKS");

				try (FileInputStream fis = new FileInputStream(keystoreFile))
				{
					keyStore.load(fis, password);
				}

				return (SecretKey) keyStore.getKey(keyAlias, password);
			} catch (IOException
				| KeyStoreException
				| NoSuchAlgorithmException
				| UnrecoverableKeyException
				| CertificateException e)
			{
				throw new RuntimeException("Erro ao carregar keystore", e);
			}
		}

		// 2) arquivo base64
		String secretFile = SystemProperty.get("gate.secret-key-file").orElse(null);

		if (secretFile != null)
		{
			try
			{
				byte[] bytes = Files.readAllBytes(Paths.get(secretFile));
				byte[] decoded = Base64.getDecoder().decode(bytes);
				return Keys.hmacShaKeyFor(decoded);
			} catch (WeakKeyException | IOException e)
			{
				throw new RuntimeException("Erro ao ler secret-key-file", e);
			}
		}

		// 3) system property base64
		String secret = SystemProperty.get("gate.secret-key").orElse(null);

		if (secret != null)
		{
			byte[] decoded = Base64.getDecoder().decode(secret);
			return Keys.hmacShaKeyFor(decoded);
		}

		// 4) fallback
		return Jwts.SIG.HS256.key().build();
	}

	// =========================
	// derivação
	// =========================
	private static byte[] derive(SecretKey master, String info)
	{
		try
		{
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(master);
			return mac.doFinal(info.getBytes(StandardCharsets.UTF_8));
		} catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException e)
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
		return new SecretKeySpec(
			Arrays.copyOf(derive(master, info), 32),
			"AES"
		);
	}

	@Produces
	@Dependent
	public CryptoKeys produce()
	{
		return KEYS;
	}
}
