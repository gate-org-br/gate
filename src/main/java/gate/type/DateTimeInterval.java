package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DateTimeIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;

@Icon("2003")
@Converter(DateTimeIntervalConverter.class)
public class DateTimeInterval implements Serializable, Comparable<DateTimeInterval>
{

	private final DateTime dateTime1;

	private final DateTime dateTime2;

	public DateTimeInterval(final DateTime dateTime1, final DateTime dateTime2)
	{
		if (dateTime1.getValue() > dateTime2.getValue())
			throw new IllegalArgumentException("dateTime1 must be <= dateTime2");
		if (dateTime2.getValue() < dateTime1.getValue())
			throw new IllegalArgumentException("dateTime2 must be >= dateTime1");
		this.dateTime1 = dateTime1;
		this.dateTime2 = dateTime2;
	}

	public DateTimeInterval(String format, String dateTime1, String dateTime2) throws ParseException
	{
		this(new DateTime(format, dateTime1), new DateTime(format, dateTime2));
	}

	public DateTimeInterval(String dateTime1, String dateTime2) throws ParseException
	{
		this(new DateTime(dateTime1), new DateTime(dateTime2));
	}

	public DateTimeInterval(String string) throws ParseException
	{
		String value = string.replaceAll("[^0123456789]", "");
		if (value.length() != 24)
			throw new IllegalArgumentException(String.format("%s não é um intervalo de datas/horas válido.", string));
		this.dateTime1 = new DateTime(value.substring(0, 12));
		this.dateTime2 = new DateTime(value.substring(12, 24));
		if (dateTime1.getValue() > dateTime2.getValue())
			throw new IllegalArgumentException("dateTime1 must be <= dateTime2");
		if (dateTime2.getValue() < dateTime1.getValue())
			throw new IllegalArgumentException("dateTime2 must be >= dateTime1");
	}

	public boolean contains(DateTime dateTime)
	{
		return getDateTime1().compareTo(dateTime) <= 0 && getDateTime2().compareTo(dateTime) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DateTimeInterval && ((DateTimeInterval) obj).dateTime1.equals(dateTime1) && ((DateTimeInterval) obj).dateTime2.equals(dateTime2));
	}

	public String format(String format)
	{
		return String.format("%s - %s", getDateTime1(), getDateTime2());
	}

	public DateTime getDateTime1()
	{
		return dateTime1;
	}

	public DateTime getDateTime2()
	{
		return dateTime2;
	}

	public DateInterval getDateInterval()
	{
		return new DateInterval(dateTime1.getDate(), dateTime2.getDate());
	}

	@Override
	public int hashCode()
	{
		return (int) (dateTime1.getValue() + dateTime2.getValue());
	}

	@Override
	public String toString()
	{
		return format("dd/MM/yyyy HH:mm");
	}

	public long getValue()
	{
		return getDateTime2().getValue() - getDateTime1().getValue();
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
}
