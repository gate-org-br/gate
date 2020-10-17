package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DateConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Icon("2003")
@Converter(DateConverter.class)
public final class Date implements Comparable<Date>, Serializable
{

	private static final String FORMAT = "dd/MM/yyyy";
	private static final long serialVersionUID = 1L;

	private final long value;

	public Date(long value)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.value = calendar.getTimeInMillis();
	}

	public Month getMonthYear()
	{
		return Month.of(getMonth().getValue(), getYear().getValue());
	}

	public DateTime with(Time time)
	{
		return DateTime.of(this, time);
	}

	@Override
	public int compareTo(Date date)
	{
		return Long.compare(getValue(), date.getValue());
	}

	@Override
	public boolean equals(Object date)
	{
		return date instanceof Date && ((Date) date).getValue() == getValue();
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

	public Field getMonth()
	{
		return new Field(Calendar.MONTH);
	}

	public long getValue()
	{
		return value;
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
		return Date.formatter(FORMAT).format(this);
	}

	public static Date now()
	{
		return new Date(System.currentTimeMillis());
	}

	public static Date of(int day, int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Date(calendar.getTimeInMillis());
	}

	public static Date of(String string) throws ParseException
	{
		return Date.formatter(FORMAT).parse(string);
	}

	public static Date of(Calendar calendar)
	{
		return new Date(calendar.getTimeInMillis());
	}

	public static Date of(java.util.Date date)
	{
		return new Date(date.getTime());
	}

	public boolean lt(Date date)
	{
		return compareTo(date) < 0;
	}

	public boolean le(Date date)
	{
		return compareTo(date) <= 0;
	}

	public boolean eq(Date date)
	{
		return compareTo(date) == 0;
	}

	public boolean ne(Date date)
	{
		return compareTo(date) != 0;
	}

	public boolean ge(Date date)
	{
		return compareTo(date) >= 0;
	}

	public boolean gt(Date date)
	{
		return compareTo(date) > 0;
	}

	public Duration getDuration(Date date)
	{
		return Duration.of(Math.abs(getValue() - date.getValue()) / 1000);
	}

	public static Formatter<Date> formatter(String format)
	{
		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat(format);

		return new Formatter<Date>()
		{
			@Override
			public Date parse(String source) throws ParseException
			{
				return Date.of(SimpleDateFormat.parse(source));
			}

			@Override
			public String format(Date source)
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

		public Date getMin()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMinimum(id));
			return new Date(calendar.getTimeInMillis());
		}

		public Date getMax()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMaximum(id));
			return new Date(calendar.getTimeInMillis());
		}

		public DateInterval getDateInterval()
		{
			return new DateInterval(getMin(), getMax());
		}

		public List<Date> getAll()
		{
			Calendar calendar = toCalendar();
			List<Date> dates = new ArrayList<>();
			for (int i = calendar.getActualMinimum(id); i <= calendar.getActualMaximum(id); i++)
			{
				calendar.set(id, i);
				dates.add(new Date(calendar.getTimeInMillis()));
			}
			return dates;
		}

		public Date add(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, value);
			return new Date(calendar.getTimeInMillis());
		}

		public Date rem(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, -value);
			return new Date(calendar.getTimeInMillis());
		}

		public Date roll(int value)
		{
			Calendar calendar = toCalendar();
			calendar.roll(id, value);
			return new Date(calendar.getTimeInMillis());
		}

		public Date set(int value)
		{
			Calendar calendar = toCalendar();
			calendar.set(id, value);
			return new Date(calendar.getTimeInMillis());
		}

		@Override
		public String toString()
		{
			return String.valueOf(getValue());
		}
	}
}
