package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DateIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

@Icon("2003")
@Converter(DateIntervalConverter.class)
public final class DateInterval implements Serializable, Comparable<DateInterval>
{

	private static final long serialVersionUID = 1L;

	private final Date date1;

	private final Date date2;

	public DateInterval(final Date date1, final Date date2)
	{
		if (date1.getValue() > date2.getValue())
			throw new IllegalArgumentException("date1 must be <= date2");
		if (date2.getValue() < date1.getValue())
			throw new IllegalArgumentException("date2 must be >= date1");
		this.date1 = date1;
		this.date2 = date2;
	}

	public DateInterval(int year)
	{
		if (year < 0)
			throw new IllegalArgumentException("year must be >= 0");
		date1 = new Date(1, 0, year);
		date2 = new Date(31, 11, year);
	}

	public DateInterval(int year, int month)
	{
		if (year < 0)
			throw new IllegalArgumentException("year must be >= 0");
		if (month < 0)
			throw new IllegalArgumentException("month must be >= 0");
		if (month > 11)
			throw new IllegalArgumentException("month must be <= 11");

		Calendar c = Calendar.getInstance();
		c.set(year, month, 1);
		date1 = new Date(c.getTimeInMillis());
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		date2 = new Date(c.getTimeInMillis());
	}

	public DateTimeInterval toDateTimeInterval()
	{
		return new DateTimeInterval(date1.with(Time.MIN), date2.with(Time.MAX));
	}

	public DateInterval(String format, String date1, String date2) throws ParseException
	{
		this(new Date(format, date1), new Date(format, date2));
	}

	public DateInterval(String date1, String date2) throws ParseException
	{
		this(new Date(date1), new Date(date2));
	}

	public DateInterval(String string) throws ParseException
	{
		String value = string.replaceAll("[^0123456789]", "");
		if (value.length() != 16)
			throw new IllegalArgumentException(String.format("%s não é um intervalo de datas válido", string));
		this.date1 = new Date(value.substring(0, 8));
		this.date2 = new Date(value.substring(8, 16));
		if (date1.getValue() > date2.getValue())
			throw new IllegalArgumentException("date1 must be <= date2");
		if (date2.getValue() < date1.getValue())
			throw new IllegalArgumentException("date2 must be >= date1");
	}

	public boolean contains(Date date)
	{
		return getDate1().compareTo(date) <= 0 && getDate2().compareTo(date) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DateInterval && ((DateInterval) obj).date1.equals(date1) && ((DateInterval) obj).date1.equals(date1));
	}

	public String format(String format)
	{
		return String.format("%s - %s", getDate1().format(format), getDate2().format(format));
	}

	public Date getDate1()
	{
		return date1;
	}

	public Date getDate2()
	{
		return date2;
	}

	public Collection<Date> getDates()
	{
		Collection<Date> dates = new ArrayList<>();
		for (Date date = date1; date.compareTo(date2) <= 0; date = date.getDayOfMonth().add(1))
			dates.add(date);
		return dates;
	}

	@Override
	public int hashCode()
	{
		return (int) (date1.getValue() + date2.getValue());
	}

	@Override
	public String toString()
	{
		return format("dd/MM/yyyy");
	}

	public long getValue()
	{
		return getDate2().getValue() - getDate1().getValue();
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
}
