package gate.lang.expression;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Parameters
{
	
	private final Deque<Map<String, Object>> parameters = new ArrayDeque<>();
	
	public Parameters()
	{
		parameters.push(new HashMap<>());
	}
	
	public Parameters(Parameters parameters)
	{
		this.parameters.peek().putAll(parameters.parameters.peek());
	}
	
	public Parameters put(String name, Object value)
	{
		parameters.peek().put(name, value);
		return this;
	}
	
	public Object get(String name)
	{
		return parameters.peek().get(name);
	}
	
	public void remove(String name)
	{
		parameters.peek().remove(name);
	}
	
	public void push(Map<String, Object> values)
	{
		parameters.push(values);
	}
	
	public Map<String, Object> poll()
	{
		return parameters.poll();
	}
}
