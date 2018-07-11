package gate.util;

import gate.converter.Converter;
import gate.type.DateTime;
import gate.type.CNPJ;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import gate.error.AppError;
import gate.error.ConversionException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Main
{

	private static final String CHAVE = "08594785000180311220160000";

	public static void main(String[] args) throws Exception
	{
	}
}

class Key implements Serializable
{

	private CNPJ CNPJ;
	private DateTime expiration;
	public static final String ALGORITHM = "RSA";

	public Key(CNPJ CNPJ, DateTime expiration)
	{
		this.CNPJ = CNPJ;
		this.expiration = expiration;
	}

	public Key(String string)
	{
		this.CNPJ = CNPJ;
		this.expiration = expiration;
	}

	public CNPJ getCNPJ()
	{
		return CNPJ;
	}

	public void setCNPJ(CNPJ CNPJ)
	{
		this.CNPJ = CNPJ;
	}

	public DateTime getExpiration()
	{
		return expiration;
	}

	public void setExpiration(DateTime expiration)
	{
		this.expiration = expiration;
	}

	public PublicKey getPublicKey()
	{
		try (ObjectInputStream stream = new ObjectInputStream(getClass().getResourceAsStream("public.key")))
		{
			return (PublicKey) stream.readObject();
		} catch (IOException | ClassNotFoundException e)
		{
			throw new AppError(e);
		}
	}

	public PrivateKey getPrivateKey()
	{
		try (ObjectInputStream stream = new ObjectInputStream(getClass().getResourceAsStream("private.key")))
		{
			return (PrivateKey) stream.readObject();
		} catch (IOException | ClassNotFoundException e)
		{
			throw new AppError(e);
		}
	}

	public String encrypt()
	{
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
			return Converter.toString(cipher.doFinal(toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
		{
			throw new AppError(e);
		}
	}

	public Key decrypt(String string)
	{
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
			return new Key(new String(cipher.doFinal((byte[]) Converter.getConverter(byte[].class)
					.ofString(byte[].class, string)), "UTF-8"));
		} catch (ConversionException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
		{
			throw new AppError(e);
		}
	}

	public static Key decrypt(String string, String password) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(password.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
		return (Key) Converter.getConverter(Key.class).ofString(Key.class, new String(cipher.doFinal(string.getBytes(
				"UTF-8")), "UTF-8"));
	}

	@Override
	public String toString()
	{
		return String.format("%s: %s", CNPJ.getValue(), expiration.format("ddMMyyyyHHmmss"));
	}
}
