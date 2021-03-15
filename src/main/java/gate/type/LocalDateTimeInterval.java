package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.LocalDateTimeIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2003")
@Converter(LocalDateTimeIntervalConverter.class)
public final class LocalDateTimeInterval implements Serializable, Comparable<LocalDateTimeInterval>, Interval<LocalDateTime>
{

	private final LocalDateTime min;
	private final LocalDateTime max;

	private static final long serialVersionUID = 1L;
	private static final Formatter<LocalDateTimeInterval> FORMATTER = LocalDateTimeInterval.formatter("dd/MM/yyyy HH:mm");
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}) - ([0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2})");

	private LocalDateTimeInterval(LocalDateTime min, LocalDateTime max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public LocalDateTime getMin()
	{
		return min;
	}

	@Override
	public LocalDateTime getMax()
	{
		return max;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof LocalDateTimeInterval && ((LocalDateTimeInterval) obj).min.equals(min) && ((LocalDateTimeInterval) obj).min.equals(min));
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

	public java.time.Duration getDuration()
	{
		return java.time.Duration
			.ofSeconds(min.until(max, ChronoUnit.SECONDS));
	}

	@Override
	public int compareTo(LocalDateTimeInterval value)
	{
		return getDuration().compareTo(value.getDuration());
	}

	public static LocalDateTimeInterval of(String string) throws ParseException
	{
		return FORMATTER.parse(string);
	}

	public static LocalDateTimeInterval of(LocalDateTime min, LocalDateTime max)
	{
		return new LocalDateTimeInterval(min, max);
	}

	public static Formatter<LocalDateTimeInterval> formatter(String format)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return new Formatter<LocalDateTimeInterval>()
		{
			@Override
			public LocalDateTimeInterval parse(String source) throws ParseException
			{
				try
				{
					Matcher matcher = PATTERN.matcher(source.trim());
					if (!matcher.matches())
						throw new ParseException(String.format("%s não é um intervalo de datas válido", source), 0);
					return new LocalDateTimeInterval(LocalDateTime.parse(matcher.group(1), formatter),
						LocalDateTime.parse(matcher.group(2), formatter));
				} catch (IllegalArgumentException ex)
				{
					throw new ParseException(ex.getMessage(), 0);
				}
			}

			@Override
			public String format(LocalDateTimeInterval source)
			{
				return source.getMin().format(formatter) + " - " + source.getMax().format(formatter);
			}
		};
	}

	public static class Mutable
	{

		private LocalDateTime min;
		private LocalDateTime max;

		public LocalDateTime getMin()
		{
			return min;
		}

		public void setMin(LocalDateTime min)
		{
			this.min = min;
		}

		public LocalDateTime getMax()
		{
			return max;
		}

		public void setMax(LocalDateTime max)
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
