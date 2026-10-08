package gate.report;

import java.util.function.Function;

public class Value<T>
{

	private final String name;
	private final Function<T, Number> value;

	public Value(String name, Function<T, Number> value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public Function<T, Number> getValue()
	{
		return value;
	}
}
