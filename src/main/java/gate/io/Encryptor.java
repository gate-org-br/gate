package gate.io;

import gate.error.ConversionException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor
{

	private final Cipher cipher;
	private final SecretKeySpec key;

	public static Encryptor of(String algorithm, String key)
	{
		return new Encryptor(algorithm, key);
	}

	private Encryptor(String algorithm, String key)
	{
		try
		{
			cipher = Cipher.getInstance(algorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException ex)
		{
			throw new UncheckedIOException(ex.getMessage(),
				new IOException(ex));
		}

		this.key = new SecretKeySpec(Base64.getDecoder().decode(key), algorithm);
	}

	public byte[] decrypt(byte[] data) throws ConversionException
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

	public byte[] encrypt(byte[] data)
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
