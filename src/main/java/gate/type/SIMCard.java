package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.SIMCardConverter;
import java.io.Serializable;

@Converter(SIMCardConverter.class)
public class SIMCard implements Serializable
{

	private static final long serialVersionUID = 1L;

	private String value;

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
