package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.CNPJConverter;
import java.io.Serializable;

/**
 * Brazilian CNPJ
 *
 * @author davins
 */
@Converter(CNPJConverter.class)
public class CNPJ implements Serializable, Comparable<CNPJ>
{

	private static final long serialVersionUID = 1L;

	private final String value;

	/**
	 * Creates a new CNPJ with the String specified
	 *
	 * @param value CNPJ as a String
	 *
	 * @throws IllegalArgumentException if the specified String is not a
	 * valid Brazilian CNPJ
	 * @author davins
	 */
	public CNPJ(String value)
	{
		value = value.replaceAll("[^0123456789]", "");
		if (value.length() != 14)
			throw new IllegalArgumentException("value");

		int digito = 0;
		digito += Character.digit(value.charAt(0), 10) * 5;
		digito += Character.digit(value.charAt(1), 10) * 4;
		digito += Character.digit(value.charAt(2), 10) * 3;
		digito += Character.digit(value.charAt(3), 10) * 2;
		digito += Character.digit(value.charAt(4), 10) * 9;
		digito += Character.digit(value.charAt(5), 10) * 8;
		digito += Character.digit(value.charAt(6), 10) * 7;
		digito += Character.digit(value.charAt(7), 10) * 6;
		digito += Character.digit(value.charAt(8), 10) * 5;
		digito += Character.digit(value.charAt(9), 10) * 4;
		digito += Character.digit(value.charAt(10), 10) * 3;
		digito += Character.digit(value.charAt(11), 10) * 2;
		digito = 11 - (digito % 11) > 9 ? 0 : 11 - (digito % 11);
		if (Character.digit(value.charAt(12), 10) != digito)
			throw new IllegalArgumentException("valor");

		digito = 0;
		digito += Character.digit(value.charAt(0), 10) * 6;
		digito += Character.digit(value.charAt(1), 10) * 5;
		digito += Character.digit(value.charAt(2), 10) * 4;
		digito += Character.digit(value.charAt(3), 10) * 3;
		digito += Character.digit(value.charAt(4), 10) * 2;
		digito += Character.digit(value.charAt(5), 10) * 9;
		digito += Character.digit(value.charAt(6), 10) * 8;
		digito += Character.digit(value.charAt(7), 10) * 7;
		digito += Character.digit(value.charAt(8), 10) * 6;
		digito += Character.digit(value.charAt(9), 10) * 5;
		digito += Character.digit(value.charAt(10), 10) * 4;
		digito += Character.digit(value.charAt(11), 10) * 3;
		digito += Character.digit(value.charAt(12), 10) * 2;
		digito = 11 - (digito % 11) > 9 ? 0 : 11 - (digito % 11);
		if (Character.digit(value.charAt(13), 10) != digito)
			throw new IllegalArgumentException("valor");

		this.value = value;
	}

	/**
	 * Returns the CNPJ as a String without formating
	 *
	 * @return the CNPJ as a String without formating
	 * @author davins
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Returns the CNPJ as a ##.###.###/####-## formated String
	 *
	 * @author davins
	 */
	@Override
	public String toString()
	{
		return String.format("%c%c.%c%c%c.%c%c%c/%c%c%c%c-%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value
			.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12), value
			.charAt(13));
	}

	@Override
	public int compareTo(CNPJ o)
	{
		return value.compareTo(o.value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CNPJ && ((CNPJ) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));

	}
}
