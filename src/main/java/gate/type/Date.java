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

	private static final long serialVersionUID = 1L;

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

	private final long value;

	public Date()
	{
		this(System.currentTimeMillis());
	}

	public Date(Calendar calendar)
	{
		this(calendar.getTimeInMillis());
	}

	public Date(java.util.Date date)
	{
		this(date.getTime());
	}

	public Date(int day, int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.value = calendar.getTimeInMillis();
	}

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
		return new Month(getMonth().getValue(), getYear().getValue());
	}

	public Date(String format, String string) throws ParseException
	{
		this(new SimpleDateFormat(format).parse(string));
	}

	public Date(String string) throws ParseException
	{
		this("ddMMyyyy", string.replaceAll("[^0123456789]", ""));
	}

	public DateTime with(Time time)
	{
		return new DateTime(this, time);
	}

	public DateTime with(String time) throws ParseException
	{
		return new DateTime(this, new Time(time));
	}

	public DateTime with(String format, String time) throws ParseException
	{
		return new DateTime(this, new Time(format, time));
	}

	public DateTime with(int h, int m, int s)
	{
		return new DateTime(this, new Time(h, m, s, 0));
	}

	@Override
	public int compareTo(Date date)
	{
		return getValue() > date.getValue() ? 1 : getValue() < date.getValue() ? -1 : 0;
	}

	@Override
	public boolean equals(Object date)
	{
		return date instanceof Date && ((Date) date).getValue() == getValue();
	}

	public String format(String format)
	{
		return new SimpleDateFormat(format).format(new java.util.Date(getValue()));
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
		return format("dd/MM/yyyy");
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

}
