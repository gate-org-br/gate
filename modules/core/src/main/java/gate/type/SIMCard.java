package gate.type;

import java.io.Serial;

import java.io.Serializable;

public class SIMCard implements Serializable
{


	@Serial
	private static final long serialVersionUID = 1L;

	private final String value;

	public SIMCard(String value)
	{
		if (value == null
		    || !value.matches("^[0-9]{20}$"))
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return value;
	}
}