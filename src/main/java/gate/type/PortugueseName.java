package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.PortugueseNameConverter;
import gate.language.Language;
import java.util.regex.Pattern;

@Converter(PortugueseNameConverter.class)
public class PortugueseName
{

	private final String value;
	public static final Pattern PATTERN
		= Pattern.compile("^[0123456789ºªa-zA-ZàÀáÁâÂãÃéÉêÊíÍóÓôÔõÕúÚçÇ&. ]*$");

	private PortugueseName(String value)
	{
		this.value = value;
	}

	public static PortugueseName valueOf(String value)
	{
		if (value == null)
			throw new IllegalArgumentException(value + " is not a valid portuguese name");
		if (!PATTERN.matcher(value).matches())
			throw new IllegalArgumentException(value + " is not a valid portuguese name");
		return new PortugueseName(Language.PORTUGUESE.capitalize(value));
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof PortugueseName
			&& ((PortugueseName) obj).value.equals(value);
	}

	@Override
	public String toString()
	{
		return value;
	}
}
