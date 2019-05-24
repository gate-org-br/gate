package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DateIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2003")
@Converter(DateIntervalConverter.class)
public final class DateInterval implements Serializable, Comparable<DateInterval>
{

	private final Date min;
	private final Date max;

	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{4}) - ([0-9]{2}/[0-9]{2}/[0-9]{4})");

	public DateInterval(Date min, Date max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.getValue() > max.getValue())
			throw new IllegalArgumentException("min must be <= max");
		if (max.getValue() < min.getValue())
			throw new IllegalArgumentException("max must be >= min");

		this.min = min;
		this.max = max;
	}

	public DateTimeInterval toDateTimeInterval()
	{
		return new DateTimeInterval(min.with(Time.MIN), max.with(Time.MAX));
	}

	public boolean contains(Date date)
	{
		return min.compareTo(date) <= 0 && max.compareTo(date) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DateInterval && ((DateInterval) obj).min.equals(min) && ((DateInterval) obj).min.equals(min));
	}

	public String format(String format)
	{
		return String.format("%s - %s", getMin().format(format), getMax().format(format));
	}

	public Date getMin()
	{
		return min;
	}

	public Date getMax()
	{
		return max;
	}

	public Collection<Date> getDates()
	{
		Collection<Date> dates = new ArrayList<>();
		for (Date date = min; date.compareTo(max) <= 0; date = date.getDayOfMonth().add(1))
			dates.add(date);
		return dates;
	}

	@Override
	public int hashCode()
	{
		return (int) (min.getValue() + max.getValue());
	}

	@Override
	public String toString()
	{
		return String.format("%s - %s", getMin(), getMax());
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
	public int compareTo(DateInterval dateInterval)
	{
		return (int) (getValue() - dateInterval.getValue());
	}

	public static DateInterval of(int year)
	{
		if (year < 0)
			throw new IllegalArgumentException("year must be >= 0");
		return new DateInterval(Date.of(1, 0, year), Date.of(31, 11, year));
	}

	public static DateInterval of(int year, int month)
	{
		if (year < 0)
			throw new IllegalArgumentException("year must be >= 0");
		if (month < 0)
			throw new IllegalArgumentException("month must be >= 0");
		if (month > 11)
			throw new IllegalArgumentException("month must be <= 11");

		Calendar c = Calendar.getInstance();
		c.set(year, month, 1);
		Date min = new Date(c.getTimeInMillis());
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date max = new Date(c.getTimeInMillis());

		return new DateInterval(min, max);
	}

	public static DateInterval of(String string) throws ParseException
	{
		Matcher matcher = PATTERN.matcher(string.trim());
		if (!matcher.matches())
			throw new IllegalArgumentException(String.format("%s não é um intervalo de datas válido", string));

		return new DateInterval(Date.of(matcher.group(1)), Date.of(matcher.group(2)));
	}
}
