package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.LocalTimeIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2003")
@Converter(LocalTimeIntervalConverter.class)
public final class LocalTimeInterval implements Serializable, Comparable<LocalTimeInterval>, Interval<LocalTime>
{

	private final LocalTime min;
	private final LocalTime max;

	private static final long serialVersionUID = 1L;
	private static final Formatter<LocalTimeInterval> FORMATTER = LocalTimeInterval.formatter("HH:mm");
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}:[0-9]{2}) - ([0-9]{2}:[0-9]{2})");

	private LocalTimeInterval(LocalTime min, LocalTime max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public LocalTime getMin()
	{
		return min;
	}

	@Override
	public LocalTime getMax()
	{
		return max;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof LocalTimeInterval
			&& ((LocalTimeInterval) obj).min.equals(min)
			&& ((LocalTimeInterval) obj).min.equals(min));
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

	public long size()
	{
		return ChronoUnit.MICROS.between(min, max);
	}

	public java.time.Duration getDuration()
	{
		return java.time.Duration
			.ofSeconds(min.until(max, ChronoUnit.SECONDS));
	}

	@Override
	public int compareTo(LocalTimeInterval value)
	{
		return (int) (size() - value.size());
	}

	public static LocalTimeInterval of(LocalTime min, LocalTime max)
	{
		return new LocalTimeInterval(min, max);
	}

	public static LocalTimeInterval of(String string) throws ParseException
	{
		return FORMATTER.parse(string);
	}

	public static Formatter<LocalTimeInterval> formatter(String format)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return new Formatter<LocalTimeInterval>()
		{
			@Override
			public LocalTimeInterval parse(String source) throws ParseException
			{
				try
				{
					Matcher matcher = PATTERN.matcher(source.trim());
					if (!matcher.matches())
						throw new ParseException(String.format("%s não é um intervalo de horas válido", source), 0);
					return new LocalTimeInterval(LocalTime.parse(matcher.group(1), formatter), LocalTime.parse(matcher.group(2), formatter));
				} catch (IllegalArgumentException ex)
				{
					throw new ParseException(ex.getMessage(), 0);
				}
			}

			@Override
			public String format(LocalTimeInterval source)
			{
				return source.getMin().format(formatter) + " - " + source.getMax().format(formatter);
			}
		};
	}

	public static class Mutable
	{

		private LocalTime min;
		private LocalTime max;

		public LocalTime getMin()
		{
			return min;
		}

		public void setMin(LocalTime min)
		{
			this.min = min;
		}

		public LocalTime getMax()
		{
			return max;
		}

		public void setMax(LocalTime max)
		{
			this.max = max;
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Mutable
				&& Objects.equals(min, ((Mutable) obj).min)
				&& Objects.equals(max, ((Mutable) obj).max);
		}

		@Override
		public int hashCode()
		{
			return (min != null ? min.hashCode() : 0)
				+ (max != null ? max.hashCode() : 0);
		}
	}

}
