package gate.lang.expression;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Parameters
{

	private final Map<String, Deque<Object>> parameters = new HashMap();

	public Parameters()
	{
	}

	public Parameters(Parameters parameters)
	{
		this.parameters.putAll(parameters.parameters);
	}

	public Parameters put(String name, Object value)
	{
		parameters.computeIfAbsent(name,
			e -> new ArrayDeque<>()).push(value);
		return this;
	}

	public Object get(String name)
	{
		Deque deque = parameters.get(name);
		if (deque == null)
			throw new NoSuchElementException(name);
		return deque.peek();
	}

	public void remove(String name)
	{
		Deque deque = parameters.get(name);
		if (deque == null)
			throw new NoSuchElementException(name);
		deque.remove();
		if (deque.isEmpty())
			parameters.remove(name);
	}
}
