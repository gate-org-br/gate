package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.SafeNameConverter;
import gate.handler.SafeNameHandler;
import gate.language.Language;

import java.text.Normalizer;
import java.util.Objects;
import java.util.regex.Pattern;

@Handler(SafeNameHandler.class)
@Converter(SafeNameConverter.class)
public class SafeName
{
	public static final Pattern PATTERN = Pattern
			.compile("^[\\p{L}\\p{N}]+(?:[ -][\\p{L}\\p{N}]+)*$");

	private static final Pattern SPACE_PATTERN = Pattern.compile(" +");

	private final String value;

	private SafeName(String value) {this.value = value;}

	@Override
	public String toString() {return value;}

	@Override
	public int hashCode() {return value.hashCode();}

	@Override
	public boolean equals(Object o) {return o instanceof SafeName s && s.value.equals(value);}

	public static SafeName valueOf(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("value is null");

		value = Normalizer.normalize(value.trim(), Normalizer.Form.NFC);
		value = SPACE_PATTERN.matcher(value).replaceAll(" ");

		if (!PATTERN.matcher(value).matches())
			throw new IllegalArgumentException("invalid SafeName: " + value);

		return new SafeName(value);
	}

	public SafeName format(Language language)
	{
		Objects.requireNonNull(language, "language");
		return valueOf(language.capitalize(value));
	}
}