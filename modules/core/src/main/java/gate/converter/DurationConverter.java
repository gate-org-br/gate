package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Description("Duração no formato dD hH mM sS onde d, h, m e s são o número de dias, horas, minutos e segundos respectivamente")
public class DurationConverter implements Converter
{

	private static final Pattern ALTERNATIVE = Pattern.compile("^ *([0-9]{2}):([0-9]{2})(:([0-9]{2}))? *$");
	private static final Pattern PATTERN = Pattern.compile("^ *(([0-9]+)[dD])? *(([0-9]+)[hH])? *(([0-9]+)[mM])? *(([0-9]+)[sS])?|([0-9]+) *$");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS = List.of(new gate.constraint.Pattern.Implementation(PATTERN.toString()));

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object)
			throws ConversionException
	{
		if (object instanceof Duration duration)
			writer.write(JsonToken.Type.NUMBER,
					String.valueOf(duration.getSeconds()));
		else
			writer.write(JsonToken.Type.NULL, null);
	}

	@Override public Object ofJson(JsonScanner jsonScanner, Type type, Type elementType) throws ConversionException
	{
		var token = jsonScanner
				.getCurrent();
		jsonScanner.scan();
		return token.getType() == JsonToken.Type.NUMBER
				? Duration.ofSeconds(Long.parseLong(token.toString())) : null;
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
	public Object ofString(Class<?> type, String string) throws ConversionException
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

	@Override
	public String render(Class<?> type, Object object)
	{
		return toString(type, object);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		Duration duration = (Duration) object;

		return switch (format.toLowerCase())
		{
			case "hh:mm" -> String.format("%02d:%02d", duration.toDaysPart() * 24 + duration.toHoursPart(), duration.toMinutesPart());
			case "hh:mm:ss" ->
					String.format("%02d:%02d:%02d", duration.toDaysPart() * 24 + duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
			case "dd:hh:mm:ss" ->
					String.format("%02d:%02d:%02d:%02d", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
			default -> String.format(format, toString(type, duration));
		};
	}
}