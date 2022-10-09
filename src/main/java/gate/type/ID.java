package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.IDConverter;
import gate.handler.IDHandler;
import java.io.Serializable;

@Handler(IDHandler.class)
@Converter(IDConverter.class)
public class ID implements Serializable, Comparable<ID>
{

	private static final long serialVersionUID = 1L;

	private final int value;

	public ID(int value)
	{
		if (value < 0)
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	public ID(String string)
	{
		this(Integer.parseInt(string));
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ID && value == ((ID) obj).getValue();
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return String.format("%010d", value);
	}

	@Override
	public int compareTo(ID id)
	{
		return this.value - id.value;
	}

	public static ID of(int value)
	{
		return new ID(value);
	}

	public static ID of(String value)
	{
		return new ID(value);
	}
}
