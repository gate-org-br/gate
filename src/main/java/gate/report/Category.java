package gate.report;

import java.util.function.Function;

public class Category<T>
{

	private final String name;
	private final Function<T, String> value;

	public Category(String name, Function<T, String> value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public Function<T, String> getValue()
	{
		return value;
	}
}
