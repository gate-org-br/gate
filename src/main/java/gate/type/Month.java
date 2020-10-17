package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.MonthConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Icon("2003")
@Converter(MonthConverter.class)
public final class Month implements Comparable<Month>, Serializable
{

	private static final String FORMAT = "MM/yyyy";

	private static final long serialVersionUID = 1L;

	private final long value;

	public Collection<Date> getDates()
	{
		return new Date(getValue()).getDayOfMonth().getAll();
	}

	public DateInterval getDateInterval()
	{
		return new Date(getValue()).getDayOfMonth().getDateInterval();
	}

	public DateTimeInterval getDateTimeInterval()
	{
		return new Date(getValue()).getDayOfMonth()
			.getDateInterval().toDateTimeInterval();
	}

	public Month(long value)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.value = calendar.getTimeInMillis();
	}

	@Override
	public int compareTo(Month month)
	{
		return Long.compare(getValue(), month.getValue());
	}

	@Override
	public boolean equals(Object month)
	{
		return month instanceof Month && ((Month) month).getValue() == getValue();
	}

	public String format(String format)
	{
		return new SimpleDateFormat(format).format(new java.util.Date(getValue()));
	}

	public Field getEra()
	{
		return new Field(Calendar.ERA);
	}

	public Field getYear()
	{
		return new Field(Calendar.YEAR);
	}

	public Field getMonth()
	{
		return new Field(Calendar.MONTH);
	}

	public Date with(int day)
	{
		Calendar calendar = toCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return new Date(calendar.getTimeInMillis());
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
		return Month.formatter(FORMAT).format(this);
	}

	public Duration getDuration(Month month)
	{
		return Duration.of(Math.abs(getValue() - month.getValue()) / 1000);
	}

	public static Month now()
	{
		return new Month(System.currentTimeMillis());
	}

	public static Month of(int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Month(calendar.getTimeInMillis());
	}

	public static Month of(String string) throws ParseException
	{
		return Month.formatter(FORMAT).parse(string);
	}

	public static Month of(Calendar calendar)
	{
		return new Month(calendar.getTimeInMillis());
	}

	public static Month of(java.util.Date date)
	{
		return new Month(date.getTime());
	}

	public static Formatter<Month> formatter(String format)
	{
		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat(format);

		return new Formatter<Month>()
		{
			@Override
			public Month parse(String source) throws ParseException
			{
				return Month.of(SimpleDateFormat.parse(source));
			}

			@Override
			public String format(Month source)
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

		public Month getMin()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMinimum(id));
			return new Month(calendar.getTimeInMillis());
		}

		public Month getMax()
		{
			Calendar calendar = toCalendar();
			calendar.set(id, calendar.getActualMaximum(id));
			return new Month(calendar.getTimeInMillis());
		}

		public MonthInterval getInterval()
		{
			return new MonthInterval(getMin(), getMax());
		}

		public List<Month> getAll()
		{
			Calendar calendar = toCalendar();
			List<Month> dates = new ArrayList<>();
			for (int i = calendar.getActualMinimum(id); i <= calendar.getActualMaximum(id); i++)
			{
				calendar.set(id, i);
				dates.add(new Month(calendar.getTimeInMillis()));
			}
			return dates;
		}

		public Month add(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, value);
			return new Month(calendar.getTimeInMillis());
		}

		public Month rem(int value)
		{
			Calendar calendar = toCalendar();
			calendar.add(id, -value);
			return new Month(calendar.getTimeInMillis());
		}

		public Month roll(int value)
		{
			Calendar calendar = toCalendar();
			calendar.roll(id, value);
			return new Month(calendar.getTimeInMillis());
		}

		public Month set(int value)
		{
			Calendar calendar = toCalendar();
			calendar.set(id, value);
			return new Month(calendar.getTimeInMillis());
		}
	}

}
