package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.RangeConverter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.PrimitiveIterator.OfLong;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

@Converter(RangeConverter.class)
public class Range implements Iterable<Long>, Serializable
{

	private static final long serialVersionUID = 1L;

	private final long min;
	private final long max;

	private static final Pattern PATTERN = Pattern.compile("^ *([0-9]+) *([-] *([0-9]+))? *$");

	private Range(long min, long max)
	{
		if (min < 0)
			throw new IllegalArgumentException("min must be greater than 0");
		if (max < 0)
			throw new IllegalArgumentException("max must be greater than 0");
		if (max < min)
			throw new IllegalArgumentException("max must be greater or equals to min");
		this.min = min;
		this.max = max;
	}

	public long getMin()
	{
		return min;
	}

	public long getMax()
	{
		return max;
	}

	public boolean contains(long value)
	{
		return min <= value
			&& value <= max;
	}

	@Override
	public String toString()
	{
		if (min == max)
			return String
				.valueOf(min);
		else
			return min + " - " + max;
	}

	public static Range of(long value)
	{
		return new Range(value, value);
	}

	public static Range of(long min, long max)
	{
		return new Range(min, max);
	}

	public static Range of(String value) throws ParseException
	{
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.matches())
			throw new ParseException(value + " is not a valid range", 0);

		String min = matcher.group(1);
		String max = matcher.group(3);
		return of(Long.parseLong(min),
			Long.parseLong(max != null ? max : min));
	}

	@Override
	public void forEach(Consumer<? super Long> action)
	{
		for (long i = min; i <= max; i++)
			action.accept(i);
	}

	public void forEach(LongConsumer action)
	{
		for (long i = min; i <= max; i++)
			action.accept(i);
	}

	public LongStream stream()
	{
		return StreamSupport.longStream(spliterator(), false);
	}

	public long size()
	{
		return max - min + 1;
	}

	@Override
	public OfLong iterator()
	{
		return new OfLong()
		{
			private long value = min;

			@Override
			public boolean hasNext()
			{
				return value <= max;
			}

			@Override
			public Long next()
			{
				return value++;
			}

			@Override
			public void forEachRemaining(Consumer<? super Long> action)
			{
				while (value <= max)
					action.accept(value++);
			}

			@Override
			public long nextLong()
			{
				return value++;
			}

			@Override
			public void forEachRemaining(LongConsumer action)
			{
				while (value <= max)
					action.accept(value++);
			}
		};
	}

	@Override
	public Spliterator.OfLong spliterator()
	{
		return new Spliterator.OfLong()
		{
			private long value = min;

			@Override
			public Spliterator.OfLong trySplit()
			{
				return null;
			}

			@Override
			public boolean tryAdvance(LongConsumer action)
			{
				if (value > max)
					return false;
				action.accept(value++);
				return true;
			}

			@Override
			public void forEachRemaining(LongConsumer action)
			{
				while (value <= max)
					action.accept(value++);
			}

			@Override
			public boolean tryAdvance(Consumer<? super Long> action)
			{
				if (value > max)
					return false;
				action.accept(value++);
				return true;
			}

			@Override
			public void forEachRemaining(Consumer<? super Long> action)
			{
				while (value <= max)
					action.accept(value++);
			}

			@Override
			public long estimateSize()
			{
				return max - min + 1;
			}

			@Override
			public long getExactSizeIfKnown()
			{
				return max - min + 1;
			}

			@Override
			public int characteristics()
			{
				return Spliterator.DISTINCT
					| Spliterator.IMMUTABLE
					| Spliterator.NONNULL
					| Spliterator.ORDERED
					| Spliterator.SIZED
					| Spliterator.SORTED;
			}

			@Override
			public Comparator<? super Long> getComparator()
			{
				return null;
			}
		};
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Range
			&& ((Range) obj).min == min
			&& ((Range) obj).max == max;
	}

	@Override
	public int hashCode()
	{
		return (int) min + (int) max;
	}
}
