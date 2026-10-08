package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationConverter implements Converter
{
	private static final Pattern ALTERNATIVE = Pattern.compile("^ *([0-9]{2}):([0-9]{2})(:([0-9]{2}))? *$");
	private static final Pattern PATTERN = Pattern.compile("^ *(([0-9]+)[dD])? *(([0-9]+)[hH])? *(([0-9]+)[mM])? *(([0-9]+)[sS])?|([0-9]+) *$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= List.of(new gate.constraint.Pattern.Implementation("^\\s*(?:([0-9]+[dD])?\\s*([0-9]+[hH])?\\s*([0-9]+[mM])?\\s*([0-9]+[sS])?|[0-9]+|[0-9]{2}:[0-9]{2}(?::[0-9]{2})?)\\s*$"));

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		Duration duration = (Duration) object;
		StringJoiner string = new StringJoiner(" ");
		if (duration.toDaysPart() > 0)
			string.add(duration.toDaysPart() + "d");
		if (duration.toHoursPart() > 0)
			string.add(duration.toHoursPart() + "h");
		if (duration.toMinutesPart() > 0)
			string.add(duration.toMinutesPart() + "m");
		if (duration.toSecondsPart() > 0)
			string.add(duration.toSecondsPart() + "s");
		return string.toString();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string.isBlank())
			return null;

		Matcher matcher = PATTERN.matcher(string);
		if (matcher.matches())
		{
			if (matcher.group(9) != null)
				return Duration.ofMinutes(Integer.parseInt(matcher.group(9)));
			int d = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
			int h = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
			int m = matcher.group(6) != null ? Integer.parseInt(matcher.group(6)) : 0;
			int s = matcher.group(8) != null ? Integer.parseInt(matcher.group(8)) : 0;

			return Duration.ofSeconds((long) d * 24 * 60 * 60 + (long) h * 60 * 60 + m * 60L + s);
		}

		matcher = ALTERNATIVE.matcher(string);
		if (!matcher.matches())
			throw new ConversionException(string + " não é uma duração válida");

		int h = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
		int m = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
		int s = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
		return Duration.ofSeconds((long) h * 60 * 60 + m * 60L + s);
	}

}
