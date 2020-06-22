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
		if (!CNPJ.validate(value))
			throw new IllegalArgumentException(value + " is not a valid Brazilian CNPJ");

		this.value = value.codePoints().filter(e -> Character.isDigit(e))
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}

	/**
	 * Creates a new CNPJ with the String specified.
	 *
	 * @param value CNPJ as a String
	 * @return the new CNPJ created
	 *
	 * @throws IllegalArgumentException if the specified String is not a
	 * valid Brazilian CNPJ
	 */
	public static CPF of(String value)
	{
		return new CPF(value);
	}

	/**
	 * Checks if the specified value is a valid Brazilian CNPJ
	 *
	 * @param value the value to be checked
	 * @return true, only if the specified value is a valid Brazilian CNPJ
	 */
	public static boolean validate(String value)
	{
		if (value == null)
			return false;

		int[] chars = value.codePoints().filter(e -> Character.isDigit(e)).toArray();
		if (chars.length != 14)
			return false;

		int digito = 0;
		digito += Character.digit(chars[0], 10) * 5;
		digito += Character.digit(chars[1], 10) * 4;
		digito += Character.digit(chars[2], 10) * 3;
		digito += Character.digit(chars[3], 10) * 2;
		digito += Character.digit(chars[4], 10) * 9;
		digito += Character.digit(chars[5], 10) * 8;
		digito += Character.digit(chars[6], 10) * 7;
		digito += Character.digit(chars[7], 10) * 6;
		digito += Character.digit(chars[8], 10) * 5;
		digito += Character.digit(chars[9], 10) * 4;
		digito += Character.digit(chars[10], 10) * 3;
		digito += Character.digit(chars[11], 10) * 2;
		digito = 11 - (digito % 11) > 9 ? 0 : 11 - (digito % 11);
		if (Character.digit(chars[12], 10) != digito)
			return false;

		digito = 0;
		digito += Character.digit(chars[0], 10) * 6;
		digito += Character.digit(chars[1], 10) * 5;
		digito += Character.digit(chars[2], 10) * 4;
		digito += Character.digit(chars[3], 10) * 3;
		digito += Character.digit(chars[4], 10) * 2;
		digito += Character.digit(chars[5], 10) * 9;
		digito += Character.digit(chars[6], 10) * 8;
		digito += Character.digit(chars[7], 10) * 7;
		digito += Character.digit(chars[8], 10) * 6;
		digito += Character.digit(chars[9], 10) * 5;
		digito += Character.digit(chars[10], 10) * 4;
		digito += Character.digit(chars[11], 10) * 3;
		digito += Character.digit(chars[12], 10) * 2;
		digito = 11 - (digito % 11) > 9 ? 0 : 11 - (digito % 11);
		return Character.digit(chars[13], 10) == digito;
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
	 * @return the CNPJ as a ##.###.###/####-## formated String
	 */
	@Override
	public String toString()
	{
		return value.substring(0, 2)
			+ "." + value.substring(2, 5)
			+ "." + value.substring(5, 8)
			+ "/" + value.substring(8, 12)
			+ "-" + value.substring(12);
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
		return Integer.parseInt(value);

	}
}
