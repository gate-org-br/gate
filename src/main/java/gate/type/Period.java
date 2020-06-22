package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.PeriodConverter;
import static gate.type.TimeAmount.H;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Converter(PeriodConverter.class)
public class Period extends TimeAmount
{

	public static final Period ZERO = new Period(0);
	public static final Period HALF_MINUTE = new Period(30);
	public static final Period MINUTE = new Period(60);
	public static final Period HALF_HOUR = new Period(1800);
	public static final Period HOUR = new Period(3600);
	public static final Period HALF_DAY = new Period(43200);
	public static final Period DAY = new Period(86400);

	private static final long serialVersionUID = 1L;
	private static final Pattern DEL = Pattern.compile(":");
	private static final Pattern VAL = Pattern.compile("^[0-9][0-9]*:[0-9][0-9]:[0-9][0-9]$");

	private Period(long value)
	{
		super(value);
	}

	public static Period of(long value)
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
				return new Period(value);
		}
	}

	public static Period of(Duration duration)
	{
		return duration != null
				? Period.of(duration.getValue()) : null;
	}

	public static Period of(long h, long m, long s)
	{
		return of((h * H) + (m * M) + (s * S));
	}

	public static Period of(DateTime dateTime1,
			DateTime dateTime2)
	{
		return Period.of(Math.abs(dateTime2.getValue()
				- dateTime1.getValue()) / 1000);
	}

	public Duration toDuration()
	{
		return Duration.of(this);
	}

	public static Period of(String string)
	{
		long value = 0;
		if (!VAL.matcher(string).matches())
			throw new IllegalArgumentException(String.format("%s is not a valid duraton", string));
		String[] strings = DEL.split(string);
		value += Integer.valueOf(strings[2]) * S;
		value += Integer.valueOf(strings[1]) * M;
		value += Integer.valueOf(strings[0]) * H;
		return of(value);
	}

	@Override
	public String toString()
	{
		return String.format("%02d:%02d:%02d", getH(), getM(), getS());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Period && ((Period) obj).value == value;
	}

	@Override
	public int hashCode()
	{
		return (int) value;
	}

	@Override
	public Period sub(TimeAmount o)
	{
		return new Period(Math.max(0, value - o.value));
	}

	@Override
	public Period add(TimeAmount o)
	{
		return new Period(value + o.value);
	}

	@Override
	public Period multiply(Integer value)
	{
		return new Period(getValue() * value);
	}

	@Override
	public Period divide(Integer value)
	{
		return value == 0 ? Period.ZERO : new Period(getValue() / value);
	}

	public static <T> Period sum(Stream<Period> durations)
	{
		return new Period(durations.filter(Objects::nonNull).mapToLong(Period::getValue).sum());
	}

	public static <T> Period avg(Stream<Period> durations)
	{
		return new Period((long) durations.filter(Objects::nonNull).mapToLong(Period::getValue).average().orElse(0));
	}

	public static <T> Period max(Stream<Period> durations)
	{
		return new Period(durations.filter(Objects::nonNull).mapToLong(Period::getValue).max().orElse(0));
	}

	public static <T> Period min(Stream<Period> durations)
	{
		return new Period(durations.filter(Objects::nonNull).mapToLong(Period::getValue).max().orElse(0));
	}
}
