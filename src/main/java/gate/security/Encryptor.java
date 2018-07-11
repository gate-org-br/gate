package gate.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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

	public Encryptor(String algorithm, byte[] key) throws SecurityException
	{
		try
		{
			cipher = Cipher.getInstance(algorithm);
			this.key = new SecretKeySpec(key, algorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException ex)
		{
			throw new SecurityException(ex.getMessage(), ex);
		}

	}

	public Encryptor(String algorithm, String key) throws SecurityException
	{
		this(algorithm, DatatypeConverter.parseBase64Binary(key));
	}

	public byte[] encrypt(byte[] data) throws SecurityException
	{
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException
				| BadPaddingException
				| InvalidKeyException ex)
		{
			throw new SecurityException(ex.getMessage(), ex);
		}
	}

	public byte[] decrypt(byte[] data) throws SecurityException
	{

		try
		{
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException
				| BadPaddingException
				| InvalidKeyException ex)
		{
			throw new SecurityException(ex.getMessage(), ex);
		}
	}

	public String decrypt(String string) throws SecurityException
	{
		return new String(decrypt(Base64.getDecoder().decode(string)));
	}

	public String encrypt(String string) throws SecurityException
	{
		return Base64.getEncoder().encodeToString(encrypt(string.getBytes()));
	}

}
