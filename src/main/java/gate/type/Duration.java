package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.DurationConverter;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Icon("2017")
@Converter(DurationConverter.class)
public class Duration extends TimeAmount
{

	private static final long serialVersionUID = 1L;
	public static final Duration ZERO = new Duration(0);
	public static final Duration HALF_MINUTE = new Duration(30);
	public static final Duration MINUTE = new Duration(60);
	public static final Duration HALF_HOUR = new Duration(1800);
	public static final Duration HOUR = new Duration(3600);
	public static final Duration HALF_DAY = new Duration(43200);
	public static final Duration DAY = new Duration(86400);

	private static final Pattern DEL = Pattern.compile(",");
	private static final Pattern REM = Pattern.compile("\\s+");
	private static final Pattern NUM = Pattern.compile("^[0-9]+$");
	private static final Pattern VAL = Pattern.compile("^[0-9]+[SMH](,[0-9]+[SMH])*$");

	private Duration(long value)
	{
		super(value);
	}

	public static Duration of(long value)
	{
		switch ((int) value)
		{
			case 0:
				return ZERO;
			case 30:
				return HALF_MINUTE;
			case 60:
				return MINUTE;
			case 1800:
				return HALF_HOUR;
			case 3600:
				return HOUR;
			case 43200:
				return HALF_DAY;
			case 86400:
				return DAY;
			default:
				return new Duration(value);
		}
	}

	public static Duration of(Period period)
	{
		return period != null
				? Duration.of(period.getValue()) : null;
	}

	public static Duration of(long h, long m, long s)
	{
		return of((h * H) + (m * M) + (s * S));
	}

	public static Duration of(DateTime dateTime1,
			DateTime dateTime2)
	{
		return Duration.of(Math.abs(dateTime2.getValue()
				- dateTime1.getValue()) / 1000);
	}

	public Period toPeriod()
	{
		return Period.of(this);
	}

	public static Duration of(String string)
	{
		string = REM.matcher(string).replaceAll("").toUpperCase();
		if (NUM.matcher(string).matches())
			string = string + "M";
		if (!VAL.matcher(string).matches())
			throw new IllegalArgumentException(string + " is not a valid duration.");

		long value = 0;
		for (String item : DEL.split(string))
			switch (item.charAt(item.length() - 1))
			{
				case 'S':
					value += Integer.parseInt(item.substring(0, item.length() - 1)) * S;
					break;
				case 'M':
					value += Integer.parseInt(item.substring(0, item.length() - 1)) * M;
					break;
				case 'H':
					value += Integer.parseInt(item.substring(0, item.length() - 1)) * H;
					break;
			}
		return of(value);
	}

	@Override
	public String toString()
	{
		if (value == 0)
			return "0";
		StringBuilder string = new StringBuilder();
		if (getH() > 0)
			string.append(string.length() == 0 ? String.format("%dh", getH()) : String.format(", %dh", getH()));
		if (getM() > 0)
			string.append(string.length() == 0 ? String.format("%dm", getM()) : String.format(", %dm", getM()));
		if (getS() > 0)
			string.append(string.length() == 0 ? String.format("%ds", getS()) : String.format(", %ds", getS()));
		return string.toString();
	}

	@Override
	public Duration sub(TimeAmount o)
	{
		return new Duration(Math.max(0, value - o.value));
	}

	@Override
	public Duration add(TimeAmount o)
	{
		return new Duration(value + o.value);
	}

	@Override
	public Duration multiply(Integer value)
	{
		return new Duration(getValue() * value);
	}

	@Override
	public Duration divide(Integer value)
	{
		return value == 0 ? Duration.ZERO : new Duration(getValue() / value);
	}

	@Override
	public int hashCode()
	{
		return (int) value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Duration && value == ((TimeAmount) obj).value;
	}

	public static <T> Duration sum(Stream<Duration> durations)
	{
		return new Duration(durations.filter(Objects::nonNull).mapToLong(Duration::getValue).sum());
	}

	public static <T> Duration avg(Stream<Duration> durations)
	{
		return new Duration((long) durations.filter(Objects::nonNull).mapToLong(Duration::getValue).average().orElse(0));
	}

	public static <T> Duration max(Stream<Duration> durations)
	{
		return new Duration(durations.filter(Objects::nonNull).mapToLong(Duration::getValue).max().orElse(0));
	}

	public static <T> Duration min(Stream<Duration> durations)
	{
		return new Duration(durations.filter(Objects::nonNull).mapToLong(Duration::getValue).max().orElse(0));
	}

}
