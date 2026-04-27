package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.error.ConversionException;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Description("Duração no formato HH:MM:SS")
public class HHMMSSDurationConverter implements Converter
{

	private static final Pattern PATTERN = Pattern.compile("^ *([0-9][0-9]):([0-5][0-9]):([0-5][0-9]) *$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= List.of(new gate.constraint.Pattern.Implementation(PATTERN.toString()), new Maxlength.Implementation(8));

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
		return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string.isBlank())
			return null;

		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches())
			throw new ConversionException(string + " não é uma duração válida");

		int h = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
		int m = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
		int s = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
		return Duration.ofSeconds((long) h * 60 * 60 + m * 60L + s);
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return toString(type, object);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return toString(type, object);
	}
}
