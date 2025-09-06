package gate.security.hash;

import gate.annotation.Converter;
import gate.converter.custom.BCryptConverter;
import gate.error.AppError;
import java.util.Objects;

@Converter(BCryptConverter.class)
public class BCrypt implements Hash
{

	private static final int ROUNDS = 12;
	private static final long serialVersionUID = 1L;

	private final String value;

	private BCrypt(String value)
	{
		this.value = value;
	}

	public static BCrypt of(String string)
	{
		return new BCrypt(string);
	}

	public static BCrypt digest(String password)
	{
		try
		{
			String hash = at.favre.lib.crypto.bcrypt.BCrypt
				.withDefaults()
				.hashToString(ROUNDS, password.toCharArray());
			return new BCrypt(hash);
		} catch (Exception ex)
		{
			throw new AppError(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean verify(String password)
	{
		try
		{
			at.favre.lib.crypto.bcrypt.BCrypt.Result result
				= at.favre.lib.crypto.bcrypt.BCrypt
					.verifyer()
					.verify(password.toCharArray(), value);
			return result.verified;
		} catch (Exception ex)
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
		return obj instanceof BCrypt
			&& Objects.equals(((BCrypt) obj).value, value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
