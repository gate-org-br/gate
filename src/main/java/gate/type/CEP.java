package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.CEPConverter;
import java.io.Serializable;

@Converter(CEPConverter.class)
public class CEP implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final String value;

	public CEP(String value)
	{
		value = value.replaceAll("[^0123456789]", "");
		if (value.length() != 8)
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CEP && ((CEP) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));
	}

	@Override
	public String toString()
	{
		return String.format("%c%c.%c%c%c-%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5), value
			.charAt(6), value.charAt(7));
	}
}
