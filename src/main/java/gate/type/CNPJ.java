package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.CNPJConverter;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Brazilian CNPJ
 */
@Converter(CNPJConverter.class)
public class CNPJ implements Serializable, Comparable<CNPJ>, BrasilianDocument
{

	public static final Pattern RAW = Pattern.compile("^[0-9]{14}$");
	public static final Pattern FORMATTED = Pattern.compile("^[0-9]{2}[.][0-9]{3}[.][0-9]{3}[/][0-9]{4}[-][0-9]{2}$");

	private static final long serialVersionUID = 1L;

	private final String value;

	private CNPJ(String value)
	{
		this.value = value;
	}

	/**
	 * Creates a new CNPJ with the String specified.
	 *
	 * @param value CNPJ as a String
	 * @return the new CNPJ created
	 *
	 * @throws IllegalArgumentException if the specified String is not a valid Brazilian CNPJ
	 */
	public static CNPJ of(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("null is not a valid CNPJ value");

		String cnpj = CNPJ.format(value);
		if (cnpj == null)
			throw new IllegalArgumentException(value + " is not a valid CNPJ value");

		return new CNPJ(cnpj);
	}

	/**
	 * Checks if the specified value is a valid Brazilian CNPJ
	 *
	 * @param value the value to be checked
	 * @return true, only if the specified value is a valid Brazilian CNPJ
	 */
	public static boolean validate(String value)
	{
		if (value != null)
			switch (value.length())
			{
				case 18:
					return value.charAt(2) == '.'
						&& value.charAt(6) == '.'
						&& value.charAt(10) == '/'
						&& value.charAt(15) == '-'
						? validate(value.charAt(0),
							value.charAt(1),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(7),
							value.charAt(8),
							value.charAt(9),
							value.charAt(11),
							value.charAt(12),
							value.charAt(13),
							value.charAt(14),
							value.charAt(16),
							value.charAt(17)) : false;
				case 14:
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
						value.charAt(10),
						value.charAt(11),
						value.charAt(12),
						value.charAt(13));

				case 13:
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
						value.charAt(9),
						value.charAt(10),
						value.charAt(11),
						value.charAt(12));
				case 12:
					return validate('0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6),
						value.charAt(7),
						value.charAt(8),
						value.charAt(9),
						value.charAt(10),
						value.charAt(11));

				case 11:
					return validate('0', '0', '0',
						value.charAt(0),
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
					return validate('0', '0', '0', '0',
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
					return validate('0', '0', '0', '0', '0',
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
					return validate('0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6),
						value.charAt(7));
				case 7:
					return validate('0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5),
						value.charAt(6));
				case 6:
					return validate('0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4),
						value.charAt(5));
				case 5:
					return validate('0', '0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3),
						value.charAt(4));
				case 4:
					return validate('0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2),
						value.charAt(3));
				case 3:
					return validate('0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1),
						value.charAt(2));
				case 2:
					return validate('0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
						value.charAt(0),
						value.charAt(1));
			}

		return false;
	}

	private static boolean validate(int c0, int c1, int c2, int c3, int c4,
		int c5, int c6, int c7, int c8, int c9, int c10, int c11, int c12, int c13)
	{
		int digito = 0;
		digito += Character.digit(c0, 10) * 5;
		digito += Character.digit(c1, 10) * 4;
		digito += Character.digit(c2, 10) * 3;
		digito += Character.digit(c3, 10) * 2;
		digito += Character.digit(c4, 10) * 9;
		digito += Character.digit(c5, 10) * 8;
		digito += Character.digit(c6, 10) * 7;
		digito += Character.digit(c7, 10) * 6;
		digito += Character.digit(c8, 10) * 5;
		digito += Character.digit(c9, 10) * 4;
		digito += Character.digit(c10, 10) * 3;
		digito += Character.digit(c11, 10) * 2;
		digito = 11 - (digito % 11) > 9 ? 0 : 11 - (digito % 11);
		if (Character.digit(c12, 10) != digito)
			return false;

		digito = 0;
		digito += Character.digit(c0, 10) * 6;
		digito += Character.digit(c1, 10) * 5;
		digito += Character.digit(c2, 10) * 4;
		digito += Character.digit(c3, 10) * 3;
		digito += Character.digit(c4, 10) * 2;
		digito += Character.digit(c5, 10) * 9;
		digito += Character.digit(c6, 10) * 8;
		digito += Character.digit(c7, 10) * 7;
		digito += Character.digit(c8, 10) * 6;
		digito += Character.digit(c9, 10) * 5;
		digito += Character.digit(c10, 10) * 4;
		digito += Character.digit(c11, 10) * 3;
		digito += Character.digit(c12, 10) * 2;
		digito = 11 - (digito % 11) > 9 ? 0 : 11 - (digito % 11);
		return Character.digit(c13, 10) == digito;
	}

	/**
	 * Checks if the specified value is a valid Brazilian CNPJ and formats it
	 *
	 * @param value the value to be formatted
	 * @return the specified value as a formatted CNPJ or null if the specified value is not a valid CNPJ
	 */
	public static String format(String value)
	{
		if (CNPJ.validate(value))
			switch (value.length())
			{
				case 18:
					return value;
				case 14:
					return value.substring(0, 2)
						+ "." + value.substring(2, 5)
						+ "." + value.substring(5, 8)
						+ "/" + value.substring(8, 12)
						+ "-" + value.substring(12);
				case 13:
					return "0" + value.substring(0, 1)
						+ "." + value.substring(1, 4)
						+ "." + value.substring(4, 7)
						+ "/" + value.substring(7, 11)
						+ "-" + value.substring(11);

				case 12:
					return "00."
						+ value.substring(0, 3)
						+ "." + value.substring(3, 6)
						+ "/" + value.substring(6, 10)
						+ "-" + value.substring(10);
				case 11:
					return "00.0"
						+ value.substring(0, 2)
						+ "." + value.substring(2, 5)
						+ "/" + value.substring(5, 9)
						+ "-" + value.substring(9);
				case 10:
					return "00.00"
						+ value.substring(0, 1)
						+ "." + value.substring(1, 4)
						+ "/" + value.substring(4, 8)
						+ "-" + value.substring(8);
				case 9:
					return "00.000."
						+ value.substring(0, 3)
						+ "/" + value.substring(3, 7)
						+ "-" + value.substring(7);
				case 8:
					return "00.000.0"
						+ value.substring(0, 2)
						+ "/" + value.substring(2, 6)
						+ "-" + value.substring(6);
				case 7:
					return "00.000.00"
						+ value.substring(0, 1)
						+ "/" + value.substring(1, 5)
						+ "-" + value.substring(5);
				case 6:
					return "00.000.000/"
						+ value.substring(0, 4)
						+ "-" + value.substring(4);
				case 5:
					return "00.000.000/0"
						+ value.substring(0, 3)
						+ "-" + value.substring(3);
				case 4:
					return "00.000.000/00"
						+ value.substring(0, 2)
						+ "-" + value.substring(2);
				case 3:
					return "00.000.000/000"
						+ value.substring(0, 1)
						+ "-" + value.substring(1);
				case 2:
					return "00.000.000/0000-" + value;
			}
		return null;
	}

	/**
	 * Returns the CNPJ as a ##.###.###/####-## formatted String
	 *
	 * @author davins
	 * @return the CNPJ as a ##.###.###/####-## formatted String
	 */
	@Override
	public String toString()
	{
		return value;
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
