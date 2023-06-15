package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.SafeStringConverter;
import gate.handler.SafeStringHandler;

@Handler(SafeStringHandler.class)
@Converter(SafeStringConverter.class)
public class SafeString
{

	private final String value;

	private SafeString(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
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
		return obj instanceof SafeString
			&& ((SafeString) obj).value.equals(value);
	}

	public static SafeString valueOf(String value)
	{
		return new SafeString(value.codePoints().filter(e ->
		{
			switch (e)
			{
				case '"':
				case '\'':
				case '\\':
				case '<':
				case '>':
					return false;

				default:
					return true;
			}

		}).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString());
	}
}
