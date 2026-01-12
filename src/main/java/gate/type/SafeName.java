package gate.type;

import java.text.Normalizer;
import java.util.regex.Pattern;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.SafeNameConverter;
import gate.handler.SafeStringHandler;

@Handler(SafeStringHandler.class)
@Converter(SafeNameConverter.class)
public class SafeName
{
	public static final Pattern PATTERN = Pattern
			.compile("^[A-Za-z0-9áàâãäéèêëíìîïóòôõöúùûüçÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ _.-]+$");

	private final String value;

	private SafeName(String value)
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
		return obj instanceof SafeName
				&& ((SafeName) obj).value.equals(value);
	}

	public static SafeName valueOf(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("value is null");

		value = Normalizer.normalize(value.trim(), Normalizer.Form.NFC);

		if (!PATTERN.matcher(value).matches())
			throw new IllegalArgumentException("invalid SafeName: " + value);

		return new SafeName(value);
	}
}
