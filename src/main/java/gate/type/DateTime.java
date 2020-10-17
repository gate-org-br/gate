package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DateTimeConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Icon("2003")
@Converter(DateTimeConverter.class)
public final class DateTime implements Comparable<DateTime>, Serializable
{

	private static final long serialVersionUID = 1L;
	private static final String FORMAT = "dd/MM/yyyy HH:mm";

	private final long value;

	public DateTime(long value)
	{
		this.value = value;
	}

	@Override
	public int compareTo(DateTime dateTime)
	{
		return Long.compare(getValue(), dateTime.getValue());
	}

	@Override
	public boolean equals(Object dateTime)
	{
		return dateTime instanceof DateTime && ((DateTime) dateTime).getValue() == getValue();
	}

	public Time getTime()
	{
		return new Time(value);
	}

	public long getValue()
	{
		return value;
	}

	public Field getAMPM()
	{
		return new Field(Calendar.AM_PM);
	}

	public Date getDate()
	{
		return new Date(value);
	}

	public Field getDayOfMonth()
	{
		return new Field(Calendar.DAY_OF_MONTH);
	}

	public Field getDayOfWeek()
	{
		return new Field(Calendar.DAY_OF_WEEK);
	}

	public Field getDayOfYear()
	{
		return new Field(Calendar.DAY_OF_YEAR);
	}

	public Field getEra()
	{
		return new Field(Calendar.ERA);
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

	public Field getMonth()
	{
		return new Field(Calendar.MONTH);
	}

	public Field getSecond()
	{
		return new Field(Calendar.SECOND);
	}

	public Field getWeekOfMonth()
	{
		return new Field(Calendar.WEEK_OF_MONTH);
	}

	public Field getWeekOfYear()
	{
		return new Field(Calendar.WEEK_OF_YEAR);
	}

	public Field getYear()
	{
		return new Field(Calendar.YEAR);
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
		return DateTime.formatter(FORMAT).format(this);
	}

	public boolean isWeekendDay()
	{
		int dayOfWeek = getDayOfWeek().getValue();
		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	public boolean isWorkingDay()
	{
		int dayOfWeek = getDayOfWeek().getValue();
		return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;
	}

	public boolean lt(DateTime dateTime)
	{
		return getValue() < dateTime.getValue();
	}

	public boolean le(DateTime dateTime)
	{
		return getValue() <= dateTime.getValue();
	}

	public boolean eq(DateTime dateTime)
	{
		return getValue() == dateTime.getValue();
	}

	public boolean ne(DateTime dateTime)
	{
		return getValue() != dateTime.getValue();
	}

	public boolean ge(DateTime dateTime)
	{
		return getValue() >= dateTime.getValue();
	}

	public boolean gt(DateTime dateTime)
	{
		return getValue() > dateTime.getValue();
	}

	public Duration getDuration(DateTime dateTime)
	{
		return Duration.of(Math.abs(getValue() - dateTime.getValue()) / 1000);
	}

	public DateTime add(Duration duration)
	{
		return new DateTime(getValue() + (duration.getValue() * 1000));
	}

	public static DateTime now()
	{
		return new DateTime(System.currentTimeMillis());
	}

	public static DateTime of(Date date, Time time)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth().getValue());
		calendar.set(Calendar.MONTH, date.getMonth().getValue());
		calendar.set(Calendar.YEAR, date.getYear().getValue());
		calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay().getValue());
		calendar.set(Calendar.MINUTE, time.getMinute().getValue());
		calendar.set(Calendar.SECOND, time.getSecond().getValue());
		calendar.set(Calendar.MILLISECOND, time.getMillisecond().getValue());
		return new DateTime(calendar.getTimeInMillis());
	}

	public static DateTime of(int day, int month, int year, int hour,
		int minute, int second, int millisecond)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
		return new DateTime(calendar.getTimeInMillis());
	}

	public static DateTime of(int day, int month, int year, int hour, int minute, int second)
	{
		return of(day, month, year, hour, minute, second, 0);
	}

	public static DateTime of(int day, int month, int year, int hour, int minute)
	{
		return of(day, month, year, hour, minute, 0, 0);
	}

	public static DateTime of(java.util.Date date)
	{
		return new DateTime(date.getTime());
	}

	public static DateTime of(Calendar calendar)
	{
		return new DateTime(calendar.getTimeInMillis());
	}

	public static DateTime of(String string) throws ParseException
	{
		return DateTime.formatter(FORMAT).parse(string);
	}

	public static Formatter<DateTime> formatter(String format)
	{
		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat(format);

		return new Formatter<DateTime>()
		{
			@Override
			public DateTime parse(String source) throws ParseException
			{
				return DateTime.of(SimpleDateFormat.parse(source));
			}

			@Override
			public String format(DateTime source)
			{
				return SimpleDateFormat.format(source.toDate());
			}
		};
	}

	public class Field implements Serializable
	{

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

		public DateTime getMin()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMinimum(id));
			return new DateTime(calendar.getTimeInMillis());
		}

		public DateTime getMax()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMaximum(id));
			return new DateTime(calendar.getTimeInMillis());
		}

		public DateTimeInterval getInterval()
		{
			return new DateTimeInterval(getMin(), getMax());
		}

		public List<DateTime> getAll()
		{
			Calendar calendar = toCalendar();
			List<DateTime> dateTimes = new ArrayList<>();
			for (int i = calendar.getActualMinimum(id); i <= calendar.getActualMaximum(id); i++)
			{
				calendar.set(id, i);
				dateTimes.add(new DateTime(calendar.getTimeInMillis()));
			}
			return dateTimes;
		}

		public DateTime roll(int value)
		{
			Calendar calendar = toCalendar();
			calendar.roll(id, value);
			return new DateTime(calendar.getTimeInMillis());
		}

		public DateTime add(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, value);
			return new DateTime(calendar.getTimeInMillis());
		}

		public DateTime rem(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, -value);
			return new DateTime(calendar.getTimeInMillis());
		}

		public DateTime set(int value)
		{
			Calendar calendar = toCalendar();
			calendar.set(id, value);
			return new DateTime(calendar.getTimeInMillis());
		}

		@Override
		public String toString()
		{
			return String.valueOf(getValue());
		}
	}
}
