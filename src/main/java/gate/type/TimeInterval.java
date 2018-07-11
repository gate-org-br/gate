package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.TimeIntervalConverter;

import java.io.Serializable;
import java.text.ParseException;

@Icon("2167")
@Converter(TimeIntervalConverter.class)
public class TimeInterval implements Serializable, Comparable<TimeInterval>
{

	private static final long serialVersionUID = 1L;
	private final Time time1;

	private final Time time2;

	public TimeInterval(String format, String time1, String time2) throws ParseException
	{
		this(new Time(format, time1), new Time(format, time2));
	}

	public TimeInterval(String time1, String time2) throws ParseException
	{
		this(new Time(time1), new Time(time2));
	}

	public TimeInterval(String string) throws ParseException
	{
		String value = string.replaceAll("[^0123456789]", "");
		if (value.length() != 8)
			throw new IllegalArgumentException(String.format("%s não é um intervalo de horas válido.", string));
		this.time1 = new Time(value.substring(0, 4));
		this.time2 = new Time(value.substring(4, 8));
		if (time1.getValue() > time2.getValue())
			throw new IllegalArgumentException("time1 must be <= time2");
		if (time2.getValue() < time1.getValue())
			throw new IllegalArgumentException("time2 must be >= time1");
	}

	public TimeInterval(final Time time1, final Time time2)
	{
		if (time1.getValue() > time2.getValue())
			throw new IllegalArgumentException("time1 must be <= time2");
		if (time2.getValue() < time1.getValue())
			throw new IllegalArgumentException("time2 must be >= time1");
		this.time1 = time1;
		this.time2 = time2;
	}

	public boolean contains(Time time)
	{
		return getTime1().compareTo(time) <= 0 && getTime2().compareTo(time) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof TimeInterval && ((TimeInterval) obj).time1.equals(time1) && ((TimeInterval) obj).time2.equals(time2));
	}

	public String format(String format)
	{
		return String.format("%s - %s", time1.format(format), time2.format(format));
	}

	public Time getTime1()
	{
		return time1;
	}

	public Time getTime2()
	{
		return time2;
	}

	@Override
	public int hashCode()
	{
		return (int) (time1.getValue() - time2.getValue());
	}

	@Override
	public String toString()
	{
		return format("HH:mm");
	}

	public long getValue()
	{
		return getTime2().getValue() - getTime1().getValue();
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
}
