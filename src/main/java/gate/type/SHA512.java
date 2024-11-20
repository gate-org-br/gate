package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.SHA512Converter;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Converter(SHA512Converter.class)
public class SHA512 implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final String value;

	private SHA512(String value)
	{
		this.value = value;
	}

	public static SHA512 of(String string)
	{
		return new SHA512(string);
	}

	public static SHA512 digest(String password)
	{
		return digest(password.getBytes());
	}

	public static SHA512 digest(byte[] input)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] hashBytes = digest.digest(input);
			BigInteger bigInt = new BigInteger(1, hashBytes);
			StringBuilder hashString = new StringBuilder(bigInt.toString(16));

			while (hashString.length() < 128)
				hashString.insert(0, "0");

			return new SHA512(hashString.toString());
		} catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Algoritmo SHA-512 não suportado", e);
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
		return obj instanceof SHA512
				&& Objects.equals(((SHA512) obj).value, value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
