package gate.type;

import gate.converter.Converter;
import java.util.Objects;

public class Parameter
{

	private final String name;
	private final Object value;

	public Parameter(String name, Object value)
	{
		Objects.requireNonNull(name, "Invalid parameter name");
		Objects.requireNonNull(name, "Invalid parameter value");
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return String.format("%s=%s", name, Converter.toString(value));
	}
}
