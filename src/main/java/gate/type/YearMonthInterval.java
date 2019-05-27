package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.YearMonthIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2003")
@Converter(YearMonthIntervalConverter.class)
public final class YearMonthInterval implements Serializable, Comparable<YearMonthInterval>, Interval<YearMonth>
{

	private final YearMonth min;
	private final YearMonth max;

	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{4}) - ([0-9]{2}/[0-9]{4})");
	private static final Formatter<YearMonthInterval> FORMATTER = YearMonthInterval.formatter("MM/yyyy");

	public YearMonthInterval(YearMonth min, YearMonth max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public YearMonth getMin()
	{
		return min;
	}

	@Override
	public YearMonth getMax()
	{
		return max;
	}

	@Override
	public boolean contains(YearMonth date)
	{
		return min.compareTo(date) <= 0 && max.compareTo(date) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof YearMonthInterval && ((YearMonthInterval) obj).min.equals(min) && ((YearMonthInterval) obj).min.equals(min));
	}

	@Override
	public int hashCode()
	{
		return (int) (min.hashCode() + max.hashCode());
	}

	@Override
	public String toString()
	{
		return FORMATTER.format(this);
	}

	public long size()
	{
		return ChronoUnit.MONTHS.between(min, max);
	}

	@Override
	public int compareTo(YearMonthInterval value)
	{
		return (int) (size() - value.size());
	}

	public static YearMonthInterval of(String string) throws ParseException
	{
		return FORMATTER.parse(string);
	}

	public static Formatter<YearMonthInterval> formatter(String format)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return new Formatter<YearMonthInterval>()
		{
			@Override
			public YearMonthInterval parse(String source) throws ParseException
			{
				Matcher matcher = PATTERN.matcher(source.trim());
				if (!matcher.matches())
					throw new IllegalArgumentException(String.format("%s não é um intervalo de datas válido", source));
				return new YearMonthInterval(YearMonth.parse(matcher.group(1), formatter), YearMonth.parse(matcher.group(2), formatter));
			}

			@Override
			public String format(YearMonthInterval source)
			{
				return source.getMin().format(formatter) + " - " + source.getMax().format(formatter);
			}
		};
	}
}
