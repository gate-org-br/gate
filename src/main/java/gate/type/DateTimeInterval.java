package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DateTimeIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2003")
@Converter(DateTimeIntervalConverter.class)
public class DateTimeInterval implements Serializable, Comparable<DateTimeInterval>, Interval<DateTime>
{

	private final DateTime min;

	private final DateTime max;

	private static final String FORMAT = "dd/MM/yyyy HH:mm";

	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}) - ([0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2})");

	public DateTimeInterval(final DateTime min, final DateTime max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.getValue() > max.getValue())
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public boolean contains(DateTime dateTime)
	{
		return getMin().compareTo(dateTime) <= 0 && getMax().compareTo(dateTime) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DateTimeInterval && ((DateTimeInterval) obj).min.equals(min) && ((DateTimeInterval) obj).max.equals(max));
	}

	@Override
	public DateTime getMin()
	{
		return min;
	}

	@Override
	public DateTime getMax()
	{
		return max;
	}

	public DateInterval getDateInterval()
	{
		return new DateInterval(min.getDate(), max.getDate());
	}

	@Override
	public int hashCode()
	{
		return (int) (min.getValue() + max.getValue());
	}

	@Override
	public String toString()
	{
		return DateTimeInterval.formatter(FORMAT).format(this);
	}

	public long getValue()
	{
		return getMax().getValue() - getMin().getValue();
	}

	@Override
	public int compareTo(DateTimeInterval dateTimeInterval)
	{
		return (int) (getValue() - dateTimeInterval.getValue());
	}

	public Duration getDuration()
	{
		return Duration.of(getValue() / 1000);
	}

	public Period getPeriod()
	{
		return Period.of(getValue() / 1000);
	}

	public static DateTimeInterval of(String string) throws ParseException
	{
		return DateTimeInterval.formatter(FORMAT).parse(string);
	}

	public static Formatter<DateTimeInterval> formatter(String format)
	{
		Formatter<DateTime> formatter = DateTime.formatter(format);

		return new Formatter<DateTimeInterval>()
		{
			@Override
			public DateTimeInterval parse(String source) throws ParseException
			{
				Matcher matcher = PATTERN.matcher(source.trim());
				if (!matcher.matches())
					throw new IllegalArgumentException(String.format("%s não é um intervalo de datas válido", source));
				return new DateTimeInterval(formatter.parse(matcher.group(1)), formatter.parse(matcher.group(2)));
			}

			@Override
			public String format(DateTimeInterval source)
			{
				return formatter.format(source.getMin()) + " - " + formatter.format(source.getMax());
			}
		};
	}
}
