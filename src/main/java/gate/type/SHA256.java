package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.SHA256Converter;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Converter(SHA256Converter.class)
public class SHA256 implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final String value;

	private SHA256(String value)
	{
		this.value = value;
	}

	public static SHA256 of(String string)
	{
		return new SHA256(string);
	}

	public static SHA256 digest(String password)
	{
		return digest(password.getBytes());
	}

	public static SHA256 digest(byte[] input)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input);
			BigInteger bigInt = new BigInteger(1, hashBytes);
			StringBuilder hashString = new StringBuilder(bigInt.toString(16));

			while (hashString.length() < 64)
				hashString.insert(0, "0");

			return new SHA256(hashString.toString());
		} catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Algoritmo SHA-256 não suportado", e);
		}
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof SHA256
				&& Objects.equals(((SHA256) obj).value, value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
