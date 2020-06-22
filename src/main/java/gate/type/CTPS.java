package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.CTPSConverter;
import java.io.Serializable;

@Converter(CTPSConverter.class)
public class CTPS implements Serializable, Comparable<CTPS>
{

	private static final long serialVersionUID = 1L;

	private final String value;

	public CTPS(String value)
	{
		value = value.toUpperCase().replaceAll("[^0123456789A-Z]", "");
		if (!value.matches("[0-9]{10}[A-Z]{2}"))
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	public String getNumber()
	{
		return value.substring(0, 6);
	}

	public String getSeries()
	{
		return value.substring(6, 12);
	}

	public String getState()
	{
		return value.substring(12, 14);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CTPS && ((CTPS) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value);
	}

	@Override
	public String toString()
	{
		return String.format("%c%c%c%c%c %c%c%c%c%c-%c%c", value.charAt(0),
			value.charAt(1), value.charAt(2), value.charAt(3),
			value.charAt(4), value.charAt(5), value.charAt(6),
			value.charAt(7), value.charAt(8), value.charAt(9),
			value.charAt(10), value.charAt(11));
	}

	@Override
	public int compareTo(CTPS o)
	{
		return value.compareTo(o.value);
	}
}
