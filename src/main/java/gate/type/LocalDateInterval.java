package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.LocalDateIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Icon("2003")
@Converter(LocalDateIntervalConverter.class)
public final class LocalDateInterval implements Serializable, Comparable<LocalDateInterval>, DiscreteInterval<LocalDate>
{

	private final LocalDate min;
	private final LocalDate max;

	private static final long serialVersionUID = 1L;
	private static final String FORMAT = "dd/MM/yyyy";
	private static final Formatter<LocalDateInterval> FORMATTER = LocalDateInterval.formatter(FORMAT);
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{4}) - ([0-9]{2}/[0-9]{2}/[0-9]{4})");

	private LocalDateInterval(LocalDate min, LocalDate max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public LocalDate getMin()
	{
		return min;
	}

	@Override
	public LocalDate getMax()
	{
		return max;
	}

	@Override
	public Stream<LocalDate> stream()
	{
		return min.datesUntil(max);
	}

	@Override
	public long size()
	{
		return ChronoUnit.DAYS.between(min, max);
	}

	@Override
	public boolean contains(LocalDate date)
	{
		return min.compareTo(date) <= 0 && max.compareTo(date) >= 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof LocalDateInterval && ((LocalDateInterval) obj).min.equals(min) && ((LocalDateInterval) obj).min.equals(min));
	}

	@Override
	public int hashCode()
	{
		return (int) (min.hashCode() + max.hashCode());
	}

	@Override
	public String toString()
	{
		return FORMATTER.format(this);
	}

	public java.time.Period getPeriod()
	{
		return min.until(max);
	}

	@Override
	public int compareTo(LocalDateInterval value)
	{
		return getPeriod().getDays() - value.getPeriod().getDays();
	}

	public LocalDateTimeInterval toLocalDateTimeInterval()
	{
		return LocalDateTimeInterval.of(min.atTime(LocalTime.MIN), max.atTime(LocalTime.MAX));
	}

	public static LocalDateInterval of(String string) throws ParseException
	{
		return FORMATTER.parse(string);
	}

	public static LocalDateInterval of(LocalDate min, LocalDate max)
	{
		return new LocalDateInterval(min, max);
	}

	public static Formatter<LocalDateInterval> formatter(String format)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return new Formatter<LocalDateInterval>()
		{
			@Override
			public LocalDateInterval parse(String source) throws ParseException
			{
				Matcher matcher = PATTERN.matcher(source.trim());
				if (!matcher.matches())
					throw new IllegalArgumentException(String.format("%s não é um intervalo de datas válido", source));
				return new LocalDateInterval(LocalDate.parse(matcher.group(1), formatter), LocalDate.parse(matcher.group(2), formatter));
			}

			@Override
			public String format(LocalDateInterval source)
			{
				return source.getMin().format(formatter) + " - " + source.getMax().format(formatter);
			}
		};
	}

	@Override
	public Iterator<LocalDate> iterator()
	{
		return new LocalDateIntervalIterator();
	}

	private class LocalDateIntervalIterator implements Iterator<LocalDate>
	{

		private LocalDate curr = min;

		@Override
		public boolean hasNext()
		{
			return contains(curr);
		}

		@Override
		public LocalDate next()
		{
			LocalDate next = curr;
			curr = curr.plusDays(1);
			return next;
		}
	}
}
