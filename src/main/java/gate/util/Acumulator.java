package gate.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Acumulator<K, V>
{

	private V value;
	private Object reset;
	private final V zero;
	private final BiFunction<V, V, V> function;
	private final Map<K, V> values = new HashMap<>();

	public Acumulator(V zero, BiFunction<V, V, V> function)
	{
		this.zero = zero;
		this.function = function;
	}

	public Acumulator<K, V> reset(Object reset)
	{
		if (reset.equals(this.reset))
			return this;

		value = zero;
		values.clear();
		this.reset = reset;
		return this;
	}

	public Acumulator<K, V> acumulate(K key, V value)
	{
		this.value = function.apply(getValue(), value);
		values.put(key, function.apply(getValue(key), value));
		return this;
	}

	public V getValue()
	{
		if (value == null)
			value = zero;
		return value;
	}

	public V getValue(K key)
	{
		if (!values.containsKey(key))
			values.put(key, zero);
		return values.get(key);
	}
}
