package gate.type.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Immutable tuple implementation that can take multiple values of different types.
 */
public class Tuple implements Iterable
{

	private final Map<Class, Object> values = new LinkedHashMap<>();

	private Tuple(Object... values)
	{
		for (Object value : values)
		{
			if (this.values.containsKey(value.getClass()))
				throw new IllegalArgumentException("Error trying to create tuple with values of the same type");
			this.values.put(value.getClass(), value);
		}
	}

	public static Tuple of(Object... values)
	{
		return new Tuple(values);
	}

	public <T> T get(Class<T> type)
	{
		if (!this.values.containsKey(type))
			throw new IllegalArgumentException("Error trying to access invalid value");
		return (T) values.get(type);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Tuple && ((Tuple) obj).values.equals(values);
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String toString()
	{
		return values.toString();
	}

	public Stream<Object> stream()
	{
		return values.values().stream();
	}

	public Collection<Object> values()
	{
		return values.values();
	}

	@Override
	public Iterator iterator()
	{
		return values.values().iterator();
	}

	@Override
	public void forEach(Consumer action)
	{
		values.values().forEach(action);
	}

	@Override
	public Spliterator spliterator()
	{
		return values.values().spliterator();
	}
}
