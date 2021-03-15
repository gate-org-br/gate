package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.MonthIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2003")
@Converter(MonthIntervalConverter.class)
public final class MonthInterval implements Serializable, Comparable<MonthInterval>, Interval<Month>
{

	private final Month min;
	private final Month max;

	private static final String FORMAT = "MM/yyyy";
	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{4}) - ([0-9]{2}/[0-9]{4})");

	public MonthInterval(Month min, Month max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.getValue() > max.getValue())
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public boolean contains(Month date)
	{
		return min.compareTo(date) <= 0 && max.compareTo(date) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof MonthInterval && ((MonthInterval) obj).min.equals(min) && ((MonthInterval) obj).max.equals(max));
	}

	@Override
	public Month getMin()
	{
		return min;
	}

	@Override
	public Month getMax()
	{
		return max;
	}

	@Override
	public int hashCode()
	{
		return (int) (min.getValue() + max.getValue());
	}

	@Override
	public String toString()
	{
		return MonthInterval.formatter(FORMAT).format(this);
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
	public int compareTo(MonthInterval dateInterval)
	{
		return (int) (getValue() - dateInterval.getValue());
	}

	public static MonthInterval of(int year)
	{
		if (year < 0)
			throw new IllegalArgumentException("year must be >= 0");
		return new MonthInterval(Month.of(0, year), Month.of(11, year));
	}

	public static MonthInterval of(String string) throws ParseException
	{
		return MonthInterval.formatter(FORMAT).parse(string);
	}

	public static Formatter<MonthInterval> formatter(String format)
	{
		Formatter<Month> formatter = Month.formatter(format);

		return new Formatter<MonthInterval>()
		{
			@Override
			public MonthInterval parse(String source) throws ParseException
			{
				try
				{
					Matcher matcher = PATTERN.matcher(source.trim());
					if (!matcher.matches())
						throw new ParseException(String.format("%s não é um intervalo de datas válido", source), 0);
					return new MonthInterval(formatter.parse(matcher.group(1)), formatter.parse(matcher.group(2)));
				} catch (IllegalArgumentException ex)
				{
					throw new ParseException(ex.getMessage(), 0);
				}
			}

			@Override
			public String format(MonthInterval source)
			{
				return formatter.format(source.getMin()) + " - " + formatter.format(source.getMax());
			}
		};
	}
}
