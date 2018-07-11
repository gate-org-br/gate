package gate.type;

import com.google.common.base.Objects;
import gate.annotation.Converter;
import gate.converter.custom.MD5Converter;
import gate.error.AppError;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Converter(MD5Converter.class)
public class MD5 implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final String value;

	private MD5(String value)
	{
		this.value = value;
	}

	public static MD5 of(String string)
	{
		return new MD5(string);
	}

	public static MD5 digest(String password)
	{
		try
		{
			byte[] digest = MessageDigest.getInstance("MD5")
					.digest(password.getBytes());
			BigInteger bigInt = new BigInteger(1, digest);
			String hash = bigInt.toString(16);
			while (hash.length() < 32)
				hash = "0" + hash;
			return new MD5(hash);
		} catch (NoSuchAlgorithmException ex)
		{
			throw new AppError(ex.getMessage(), ex);
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
		return obj instanceof MD5
				&& Objects.equal(((MD5) obj).value, value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
