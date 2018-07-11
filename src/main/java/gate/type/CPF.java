package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.CPFConverter;
import java.io.Serializable;

/**
 * Brazilian CPF
 *
 * @author davins
 */
@Converter(CPFConverter.class)
public class CPF implements Serializable, Cloneable, Comparable<CPF>
{

	private static final long serialVersionUID = 1L;

	private final String value;

	/**
	 * Creates a new CPF with the String specified.
	 *
	 * @param value CPF as a String
	 *
	 * @throws IllegalArgumentException if the specified String is not a
	 * valid Brazilian CPF
	 */
	public CPF(String value)
	{
		value = value.replaceAll("[^0123456789]", "");
		if (value.length() != 11)
			throw new IllegalArgumentException("value");

		int digito1 = 0;
		for (int i = 0; i < 9; i++)
			digito1 += Character.digit(value.charAt(i), 10) * (10 - i);
		digito1 = (11 - (digito1 % 11)) % 11 % 10;

		int digito2 = digito1 * 2;
		for (int i = 0; i < 9; i++)
			digito2 += Character.digit(value.charAt(i), 10) * (11 - i);
		digito2 = (11 - (digito2 % 11)) % 11 % 10;
		if (Integer.parseInt(String.valueOf(value.charAt(9))) != digito1
			|| Integer.parseInt(String.valueOf(value.charAt(10))) != digito2)
			throw new IllegalArgumentException("value");

		this.value = value;
	}

	/**
	 * Returns the CPF as a String without formating.
	 *
	 * @return the CPF as a String without formating
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Returns the CPF as a ###.###.###-## formated String.
	 */
	@Override
	public String toString()
	{
		return String.format("%c%c%c.%c%c%c.%c%c%c-%c%c", value.charAt(0),
			value.charAt(1), value.charAt(2), value.charAt(3),
			value.charAt(4), value.charAt(5), value.charAt(6),
			value.charAt(7), value.charAt(8), value.charAt(9),
			value.charAt(10));
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CPF && ((CPF) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));
	}

	@Override
	public int compareTo(CPF o)
	{
		return value.compareTo(o.value);
	}
}
