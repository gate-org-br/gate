package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.TimeConverter;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Icon("2167")
@Converter(TimeConverter.class)
public final class Time implements Comparable<Time>, Serializable
{

	public static final Time MIN = new Time(0, 0, 0, 0);
	public static final Time MAX = new Time(23, 59, 59, 0);
	private static final long serialVersionUID = 1L;

	public class Field implements Serializable
	{

		private static final long serialVersionUID = 1L;

		private final int id;

		public Field(int id)
		{
			this.id = id;
		}

		public int getId()
		{
			return id;
		}

		public String getName()
		{
			return toCalendar().getDisplayName(id, Calendar.SHORT, new Locale("pt", "br"));
		}

		public String getFullName()
		{
			return toCalendar().getDisplayName(id, Calendar.LONG, new Locale("pt", "br"));
		}

		public int getValue()
		{
			return toCalendar().get(id);
		}

		public Time getMin()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMinimum(id));
			return new Time(calendar.getTimeInMillis());
		}

		public Time getMax()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMaximum(id));
			return new Time(calendar.getTimeInMillis());
		}

		public TimeInterval getInterval()
		{
			return new TimeInterval(getMin(), getMax());
		}

		public List<Time> getAll()
		{
			Calendar calendar = toCalendar();
			List<Time> times = new ArrayList<>();
			for (int i = calendar.getActualMinimum(id); i <= calendar.getActualMaximum(id); i++)
			{
				calendar.set(id, i);
				times.add(new Time(calendar.getTimeInMillis()));
			}
			return times;
		}

		public Time add(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, value);
			return new Time(calendar.getTimeInMillis());
		}

		public Time rem(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, -value);
			return new Time(calendar.getTimeInMillis());
		}

		public Time roll(int value)
		{
			Calendar calendar = toCalendar();
			calendar.roll(id, value);
			return new Time(calendar.getTimeInMillis());
		}

		public Time set(int value)
		{
			Calendar calendar = toCalendar();
			calendar.set(id, value);
			return new Time(calendar.getTimeInMillis());
		}

		@Override
		public String toString()
		{
			return String.valueOf(getValue());
		}
	}

	private final long value;

	public Time()
	{
		this(System.currentTimeMillis());
	}

	public Time(Calendar calendar)
	{
		this(calendar.getTimeInMillis());
	}

	public Time(int hour, int minute, int second, int millisecond)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
		this.value = calendar.getTimeInMillis();
	}

	public Time(int hour, int minute, int second)
	{
		this(hour, minute, second, 0);
	}

	public Time(java.util.Date date)
	{
		this(date.getTime());
	}

	public Time(long value)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, 1970);
		this.value = calendar.getTimeInMillis();
	}

	public Time(String format, String string) throws ParseException
	{
		this(new SimpleDateFormat(format).parse(string));
	}

	public Time(String string) throws ParseException
	{
		this("HHmm", string.replaceAll("[^0123456789]", ""));
	}

	public DateTime with(Date date)
	{
		return new DateTime(date, this);
	}

	public DateTime with(String date) throws ParseException
	{
		return new DateTime(new Date(date), this);
	}

	public DateTime with(String format, String date) throws ParseException
	{
		return new DateTime(new Date(format, date), this);
	}

	public DateTime with(int d, int m, int y)
	{
		return new DateTime(new Date(d, m, y), this);
	}

	@Override
	public int compareTo(Time time)
	{
		return getValue() > time.getValue() ? 1 : getValue() < time.getValue() ? -1 : 0;
	}

	@Override
	public boolean equals(Object time)
	{
		return time instanceof Time && ((Time) time).getValue() == getValue();
	}

	public String format(String format)
	{
		return new SimpleDateFormat(format).format(new java.util.Date(getValue()));
	}

	public Field getAMPM()
	{
		return new Field(Calendar.AM_PM);
	}

	public Field getHour()
	{
		return new Field(Calendar.HOUR);
	}

	public Field getHourOfDay()
	{
		return new Field(Calendar.HOUR_OF_DAY);
	}

	public Field getMillisecond()
	{
		return new Field(Calendar.MILLISECOND);
	}

	public Field getMinute()
	{
		return new Field(Calendar.MINUTE);
	}

	public Field getSecond()
	{
		return new Field(Calendar.SECOND);
	}

	public long getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return (int) value;
	}

	public Calendar toCalendar()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getValue());
		return calendar;
	}

	public java.util.Date toDate()
	{
		return new java.util.Date(getValue());
	}

	@Override
	public String toString()
	{
		return format("HH:mm");
	}

	public boolean lt(Time time)
	{
		return compareTo(time) < 0;
	}

	public boolean le(Time time)
	{
		return compareTo(time) <= 0;
	}

	public boolean eq(Time time)
	{
		return compareTo(time) == 0;
	}

	public boolean ne(Time time)
	{
		return compareTo(time) != 0;
	}

	public boolean ge(Time time)
	{
		return compareTo(time) >= 0;
	}

	public boolean gt(Time time)
	{
		return compareTo(time) > 0;
	}

	public Duration getDuration(Time time)
	{
		return Duration.of(Math.abs(getValue() - time.getValue()) / 1000);
	}
}
