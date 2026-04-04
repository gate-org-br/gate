package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.SafeTextConverter;
import gate.handler.SafeTextHandler;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Handler(SafeTextHandler.class)
@Converter(SafeTextConverter.class)
public final class SafeText
{
	public static final Pattern PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\s.,$:;?!()-]*$");

	private final String value;

	private SafeText(String value)
	{
		this.value = value;
	}

	public static SafeText valueOf(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("value is null");

		value = Normalizer.normalize(value, Normalizer.Form.NFC)
				.replace("\r\n", "\n")
				.replace('\r', '\n')
				.trim();

		if (!PATTERN.matcher(value).matches())
			throw new IllegalArgumentException("invalid SafeText: " + value);

		return new SafeText(value);
	}

	public boolean isBlank()
	{
		return value.isBlank();
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof SafeText other && value.equals(other.value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}