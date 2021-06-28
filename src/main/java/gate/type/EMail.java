package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.EMailConverter;
import java.io.Serializable;
import java.util.regex.Pattern;

@Icon("2034")
@Converter(EMailConverter.class)
public class EMail implements Serializable
{

	private final String value;
	private static final long serialVersionUID = 1L;
	public static final String REGEX = "^[^@ ]+@[^@ ]+$";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public EMail(String value)
	{
		if (!validate(value))
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	public static boolean validate(String value)
	{
		return value != null && PATTERN.matcher(value).matches();
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
