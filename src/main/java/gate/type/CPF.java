package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.CPFConverter;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Brazilian CPF.
 */
@Converter(CPFConverter.class)
public class CPF implements Serializable, Cloneable, Comparable<CPF>, BrasilianDocument
{

	public static final Pattern RAW = Pattern.compile("^[0-9]{11}$");
	public static final Pattern FORMATTED = Pattern.compile("^[0-9]{3}[.][0-9]{3}[.][0-9]{3}[-][0-9]{2}$");

	private static final long serialVersionUID = 1L;

	private final String value;

	private CPF(String value)
	{
		this.value = value;
	}

	/**
	 * Creates a new CPF with the String specified.
	 *
	 * @param value CPF as a String
	 * @return the new CPF created
	 *
	 * @throws IllegalArgumentException if the specified String is not a valid Brazilian CPF
	 */
	public static CPF of(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("null is not a valid CPF value");

		String cpf = CPF.format(value);
		if (cpf == null)
			throw new IllegalArgumentException(value + " is not a valid CPF value");

		return new CPF(cpf);
	}

	/**
	 * Checks if the specified value is a valid Brazilian CPF
	 *
	 * @param value the value to be checked
	 * @return true, only if the specified value is a valid Brazilian CPF
	 */
	public static boolean validate(String value)
	{
		if (value != null)
			switch (value.length())
			{
				case 14:
					return value.charAt(3) == '.'
						&& value.charAt(7) == '.'
						&& value.charAt(11) == '-'
						? validate(value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6),
							value.charAt(8),
							value.charAt(9),
							value.charAt(10),
							value.charAt(12),
							value.charAt(13)) : false;
				case 11:
					return validate(value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6),
						value.charAt(7),
						value.charAt(8),
						value.charAt(9),
						value.charAt(10));

				case 10:
					return validate('0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6),
						value.charAt(7),
						value.charAt(8),
						value.charAt(9));

				case 9:
					return validate('0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6),
						value.charAt(7),
						value.charAt(8));
				case 8:
					return validate('0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6),
						value.charAt(7));
				case 7:
					return validate('0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6));
				case 6:
					return validate('0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5));
				case 5:
					return validate('0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4));
				case 4:
					return validate('0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3));
				case 3:
					return validate('0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2));
				case 2:
					return validate('0', '0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1));
			}

		return false;
	}

	/**
	 * Checks if the specified value is a valid Brazilian CPF and formats it
	 *
	 * @param value the value to be formatted
	 * @return the specified value as a formatted CPF or null if the specified value is not a valid CPF
	 */
	public static String format(String value)
	{
		if (CPF.validate(value))
			switch (value.length())
			{
				case 14:
					return value;
				case 11:
					return value.substring(0, 3) + "." + value.substring(3, 6)
						+ "." + value.substring(6, 9) + "-" + value.substring(9);
				case 10:
					return "0" + value.substring(0, 2) + "." + value.substring(2, 5)
						+ "." + value.substring(5, 8) + "-" + value.substring(8);
				case 9:
					return "00" + value.substring(0, 1) + "." + value.substring(1, 4)
						+ "." + value.substring(4, 7) + "-" + value.substring(7);
				case 8:
					return "000." + value.substring(0, 3)
						+ "." + value.substring(3, 6) + "-" + value.substring(6);
				case 7:
					return "000.0" + value.substring(0, 2)
						+ "." + value.substring(2, 5) + "-" + value.substring(5);
				case 6:
					return "000.00" + value.substring(0, 1)
						+ "." + value.substring(1, 4) + "-" + value.substring(4);
				case 5:
					return "000.000." + value.substring(0, 3) + "-" + value.substring(3);
				case 4:
					return "000.000.0" + value.substring(0, 2) + "-" + value.substring(2);
				case 3:
					return "000.000.00" + value.substring(0, 1) + "-" + value.substring(1);
				case 2:
					return "000.000.000-" + value.substring(0);
			}
		return null;
	}

	private static boolean validate(int c0, int c1, int c2, int c3, int c4,
		int c5, int c6, int c7, int c8, int c9, int c10)
	{
		int digito1 = 0;
		digito1 += Character.digit(c0, 10) * 10;
		digito1 += Character.digit(c1, 10) * 9;
		digito1 += Character.digit(c2, 10) * 8;
		digito1 += Character.digit(c3, 10) * 7;
		digito1 += Character.digit(c4, 10) * 6;
		digito1 += Character.digit(c5, 10) * 5;
		digito1 += Character.digit(c6, 10) * 4;
		digito1 += Character.digit(c7, 10) * 3;
		digito1 += Character.digit(c8, 10) * 2;
		digito1 = (11 - (digito1 % 11)) % 11 % 10;

		if (Character.digit(c9, 10) != digito1)
			return false;

		int digito2 = digito1 * 2;
		digito2 += Character.digit(c0, 10) * 11;
		digito2 += Character.digit(c1, 10) * 10;
		digito2 += Character.digit(c2, 10) * 9;
		digito2 += Character.digit(c3, 10) * 8;
		digito2 += Character.digit(c4, 10) * 7;
		digito2 += Character.digit(c5, 10) * 6;
		digito2 += Character.digit(c6, 10) * 5;
		digito2 += Character.digit(c7, 10) * 4;
		digito2 += Character.digit(c8, 10) * 3;
		digito2 = (11 - (digito2 % 11)) % 11 % 10;

		return Character.digit(c10, 10) == digito2;
	}

	/**
	 * Returns the CPF as a ###.###.###-## formatted String.
	 *
	 * @return the CPF as a ###.###.###-## formatted String.
	 */
	@Override
	public String toString()
	{
		return value;
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
