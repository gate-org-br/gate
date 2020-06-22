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
		if (!CPF.validate(value))
			throw new IllegalArgumentException(value + " is not a valid Brazilian CPF");

		this.value = value.codePoints().filter(e -> Character.isDigit(e))
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}

	/**
	 * Creates a new CPF with the String specified.
	 *
	 * @param value CPF as a String
	 * @return the new CPF created
	 *
	 * @throws IllegalArgumentException if the specified String is not a
	 * valid Brazilian CPF
	 */
	public static CPF of(String value)
	{
		return new CPF(value);
	}

	/**
	 * Checks if the specified value is a valid Brazilian CPF
	 *
	 * @param value the value to be checked
	 * @return true, only if the specified value is a valid Brazilian CPF
	 */
	public static boolean validate(String value)
	{
		if (value == null)
			return false;

		int[] chars = value.codePoints().filter(e -> Character.isDigit(e)).toArray();
		if (chars.length != 11)
			return false;

		int digito1 = 0;
		for (int i = 0; i < 9; i++)
			digito1 += Character.digit(chars[i], 10) * (10 - i);
		digito1 = (11 - (digito1 % 11)) % 11 % 10;

		int digito2 = digito1 * 2;
		for (int i = 0; i < 9; i++)
			digito2 += Character.digit(chars[i], 10) * (11 - i);
		digito2 = (11 - (digito2 % 11)) % 11 % 10;

		return Character.digit(chars[9], 10) == digito1
			&& Character.digit(chars[10], 10) == digito2;
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
	 *
	 * @return the CPF as a ###.###.###-## formated String.
	 */
	@Override
	public String toString()
	{
		return value.substring(0, 3) + "." + value.substring(3, 6)
			+ "." + value.substring(6, 9) + "-" + value.substring(9);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CPF && ((CPF) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value);
	}

	@Override
	public int compareTo(CPF o)
	{
		return value.compareTo(o.value);
	}
}
