package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.TimeIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2167")
@Converter(TimeIntervalConverter.class)
public class TimeInterval implements Serializable, Comparable<TimeInterval>
{

	private final Time min;
	private final Time max;

	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}:[0-9]{2}) - ([0-9]{2}:[0-9]{2})");

	public TimeInterval(Time min, Time max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.getValue() > max.getValue())
			throw new IllegalArgumentException("time1 must be <= time2");

		this.min = min;
		this.max = max;
	}

	public boolean contains(Time time)
	{
		return getMin().compareTo(time) <= 0 && getMax().compareTo(time) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof TimeInterval && ((TimeInterval) obj).min.equals(min) && ((TimeInterval) obj).max.equals(max));
	}

	public String format(String format)
	{
		return String.format("%s - %s", min.format(format), max.format(format));
	}

	public Time getMin()
	{
		return min;
	}

	public Time getMax()
	{
		return max;
	}

	@Override
	public int hashCode()
	{
		return (int) (min.getValue() - max.getValue());
	}

	@Override
	public String toString()
	{
		return format("HH:mm");
	}

	public long getValue()
	{
		return getMax().getValue() - getMin().getValue();
	}

	public Duration getDuration()
	{
		return Duration.of(getValue() / 1000);
	}

	@Override
	public int compareTo(TimeInterval timeInterval)
	{
		return (int) (getValue() - timeInterval.getValue());
	}

	public static TimeInterval of(String string) throws ParseException
	{
		Matcher matcher = PATTERN.matcher(string.trim());
		if (!matcher.matches())
			throw new IllegalArgumentException(String.format("%s não é um intervalo de horas válido.", string));
		return new TimeInterval(Time.of(matcher.group(1)), Time.of(matcher.group(2)));
	}
}
