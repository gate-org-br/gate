package gate.security;

import gate.cache.Cache;
import gate.util.SystemProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
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

public class CryptoKeys
{

	private static final Cache<SecretKey> MASTER_KEY = Cache.builder(() ->
	{
		String keystoreFile = SystemProperty.get("gate.key-store.file").orElse(null);
		if (keystoreFile != null)
		{
			try
			{
				String keyAlias = SystemProperty.get("gate.key-store.secret-key").orElse("secret-key");

				char[] password = SystemProperty.get("gate.key-store.password").orElse("changeit").toCharArray();

				KeyStore keyStore = KeyStore
						.getInstance(keystoreFile.toLowerCase().endsWith(".p12") ? "PKCS12" : "JCEKS");

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

		String secret = SystemProperty.get("gate.secret-key").orElse(null);
		if (secret != null)
		{
			byte[] decoded = Base64.getDecoder().decode(secret);
			return Keys.hmacShaKeyFor(decoded);
		}

		return Jwts.SIG.HS256.key().build();
	}).build();

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

	public static final Cache<SecretKey> JWS_KEY = Cache.builder(() -> Keys.hmacShaKeyFor(derive(MASTER_KEY.get(), "gate-hmac-jwt-signature"))).build();

	public static final Cache<SecretKeySpec> STATE_KEY = Cache.builder(() -> new SecretKeySpec(Arrays.copyOf(derive(MASTER_KEY.get(), "gate-aes-state-encryption"), 32), "AES")).build();
}
