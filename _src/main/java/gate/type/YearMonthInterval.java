package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.YearMonthIntervalConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Icon("2003")
@Converter(YearMonthIntervalConverter.class)
public final class YearMonthInterval implements Serializable, Comparable<YearMonthInterval>, DiscreteInterval<YearMonth>
{

	private final YearMonth min;
	private final YearMonth max;

	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN = Pattern.compile("([0-9]{2}/[0-9]{4}) - ([0-9]{2}/[0-9]{4})");
	private static final Formatter<YearMonthInterval> FORMATTER = YearMonthInterval.formatter("MM/yyyy");

	private YearMonthInterval(YearMonth min, YearMonth max)
	{
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("min must be <= max");

		this.min = min;
		this.max = max;
	}

	@Override
	public YearMonth getMin()
	{
		return min;
	}

	@Override
	public YearMonth getMax()
	{
		return max;
	}

	@Override
	public long size()
	{
		return ChronoUnit.MONTHS.between(min, max);
	}

	@Override
	public Iterator<YearMonth> iterator()
	{
		return new Iterator<YearMonth>()
		{

			private YearMonth curr = min;

			@Override
			public boolean hasNext()
			{
				return contains(curr);
			}

			@Override
			public void forEachRemaining(Consumer<? super YearMonth> action)
			{
				while (contains(curr))
				{
					action.accept(curr);
					curr = curr.plusMonths(1);
				}
			}

			@Override
			public YearMonth next()
			{
				if (!hasNext())
					throw new NoSuchElementException();
				YearMonth next = curr;
				curr = curr.plusMonths(1);
				return next;
			}
		};
	}

	@Override
	public Stream<YearMonth> stream()
	{
		return StreamSupport.stream(new Spliterator<YearMonth>()
		{
			private YearMonth curr;

			@Override
			public boolean tryAdvance(Consumer<? super YearMonth> action)
			{
				curr = curr == null ? min : curr.plusMonths(1);

				if (!contains(curr))
					return false;
				action.accept(curr);
				return true;
			}

			@Override
			public Spliterator<YearMonth> trySplit()
			{
				return null;
			}

			@Override
			public long estimateSize()
			{
				return size();
			}

			@Override
			public int characteristics()
			{
				return Spliterator.ORDERED
					| Spliterator.NONNULL
					| Spliterator.IMMUTABLE
					| Spliterator.DISTINCT
					| Spliterator.SIZED
					| Spliterator.SUBSIZED;
			}
		}, false);
	}

	@Override
	public int compareTo(YearMonthInterval value)
	{
		return (int) (size() - value.size());
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof YearMonthInterval && ((YearMonthInterval) obj).min.equals(min) && ((YearMonthInterval) obj).min.equals(min));
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

	public static YearMonthInterval of(YearMonth min, YearMonth max)
	{
		return new YearMonthInterval(min, max);
	}

	public static YearMonthInterval of(String string) throws ParseException
	{
		return FORMATTER.parse(string);
	}

	public static Formatter<YearMonthInterval> formatter(String format)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return new Formatter<YearMonthInterval>()
		{
			@Override
			public YearMonthInterval parse(String source)
			{
				Matcher matcher = PATTERN.matcher(source.trim());
				if (!matcher.matches())
					throw new IllegalArgumentException(String.format("%s não é um intervalo de datas válido", source));
				return new YearMonthInterval(YearMonth.parse(matcher.group(1), formatter), YearMonth.parse(matcher.group(2), formatter));
			}

			@Override
			public String format(YearMonthInterval source)
			{
				return source.getMin().format(formatter) + " - " + source.getMax().format(formatter);
			}
		};
	}
}
