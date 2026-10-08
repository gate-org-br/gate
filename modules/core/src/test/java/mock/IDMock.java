package mock;

import java.util.Objects;

public final class IDMock
{
	private final int value;

	private IDMock(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public static IDMock valueOf(String value)
	{
		return new IDMock(Integer.parseInt(value));
	}

	public static IDMock valueOf(int value)
	{
		return new IDMock(value);
	}

	@Override
	public String toString()
	{
		return Integer.toString(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IDMock id && value == id.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value);
	}
}
