package gate.type;

import java.io.Serial;

import java.io.Serializable;

public class SIMCard implements Serializable
{


	@Serial
	private static final long serialVersionUID = 1L;

	private final String value;

	private SIMCard(String value)
	{
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

	public static SIMCard valueOf(String string)
	{
		if (string == null
		    || !string.matches("^[0-9]{20}$"))
			throw new IllegalArgumentException("value");
		return new SIMCard(string);
	}
}