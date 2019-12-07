package gate.type;

import java.io.Serializable;

public class TomboASI implements Serializable, Cloneable, Comparable<TomboASI>
{

	private static final long serialVersionUID = 1L;

	private final String value;

	public TomboASI(String value)
	{
		if (value.matches("[0-9]{2}[-][0-9]{3}[.][0-9]{3}"))
			value = value.replaceAll("[^0123456789]", "");
		if (!value.matches("[0-9]{8}"))
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	@Override
	protected TomboASI clone()
	{
		return new TomboASI(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof TomboASI && ((TomboASI) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));
	}

	public TomboASI next()
	{
		return new TomboASI(String.format("%08d", Integer.parseInt(value) + 1));
	}

	@Override
	public String toString()
	{
		return String.format("%c%c-%c%c%c.%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5), value
			.charAt(6), value.charAt(7));
	}

	@Override
	public int compareTo(TomboASI tombo)
	{
		return value.compareTo(tombo.value);
	}

}
