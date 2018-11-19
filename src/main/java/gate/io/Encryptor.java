package gate.io;

import gate.error.ConversionException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Encryptor
{

	private final Cipher cipher;
	private final SecretKeySpec key;

	private static final Map<String, Cipher> CIPHERS
			= new ConcurrentHashMap<>();

	public static Encryptor of(String algorithm, byte[] key)
	{
		return new Encryptor(algorithm, key);
	}

	public static Encryptor of(String algorithm, String key)
	{
		return new Encryptor(algorithm,
				DatatypeConverter.parseBase64Binary(key));
	}

	private Encryptor(String algorithm, byte[] key)
	{
		cipher = CIPHERS.computeIfAbsent(algorithm, e ->
		{
			try
			{
				return Cipher.getInstance(algorithm);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException ex)
			{
				throw new UncheckedIOException(ex.getMessage(),
						new IOException(ex));
			}
		});

		this.key = new SecretKeySpec(key, algorithm);
	}

	public synchronized byte[] decrypt(byte[] data) throws ConversionException
	{

		try
		{
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException
				| BadPaddingException
				| InvalidKeyException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}

	public synchronized byte[] encrypt(byte[] data)
	{
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException
				| BadPaddingException
				| InvalidKeyException ex)
		{
			throw new UncheckedIOException(ex.getMessage(), new IOException(ex));
		}
	}

	public String encrypt(String string)
	{
		return Base64.getEncoder().encodeToString(encrypt(string.getBytes()));
	}

	public String decrypt(String string) throws ConversionException
	{
		return new String(decrypt(Base64.getDecoder().decode(string)));
	}
}
