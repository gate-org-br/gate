package gate.lang.expression;

import java.util.*;

public class Parameters
{
	private final Deque<Map<String, Object>> parameters = new ArrayDeque<>();

	public Parameters()
	{
		parameters.push(new HashMap<>());
	}

	public Parameters(Parameters parameters)
	{
		this.parameters.push(new HashMap<>());
		if (parameters != null && !parameters.parameters.isEmpty())
			Objects.requireNonNull(this.parameters.peek())
					.putAll(parameters.parameters.peek());
	}

	public Parameters put(String name, Object value)
	{
		if (parameters.isEmpty())
			parameters.push(new HashMap<>());

		var values = parameters.peek();
		if (values != null)
			values.put(name, value);
		return this;
	}

	public Object get(String name)
	{
		for (Map<String, Object> map : parameters)
			if (map.containsKey(name))
				return map.get(name);

		return null;
	}

	public void push(Map<String, Object> values)
	{
		parameters.push(values);
	}

	public Map<String, Object> poll()
	{
		return parameters.size() > 1 ? parameters.poll() : parameters.peek();
	}
}