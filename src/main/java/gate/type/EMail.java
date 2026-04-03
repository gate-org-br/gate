package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.EMailConverter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Pattern;

@Icon("2034")
@Converter(EMailConverter.class)
public class EMail implements Serializable
{

	private final String value;

	@Serial
	private static final long serialVersionUID = 1L;
	public static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,63}$");

	public EMail(String value)
	{
		value = normalize(value);
		if (!validate(value))
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	public static boolean validate(String value)
	{
		return value != null && PATTERN.matcher(value).matches();
	}

	private static String normalize(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("value");
		return value.trim().toLowerCase(Locale.ROOT);
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof EMail && obj.toString().equals(toString());
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}