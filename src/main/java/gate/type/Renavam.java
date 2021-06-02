package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.RenavamConverter;
import java.io.Serializable;
import java.util.Objects;

/**
 * Brazilian Renavam.
 */
@Converter(RenavamConverter.class)
public class Renavam implements Serializable, Cloneable, Comparable<Renavam>
{

	private static final long serialVersionUID = 1L;

	private final String value;

	private Renavam(String value)
	{
		this.value = value;
	}

	/**
	 * Creates a new Renavam with the String specified.
	 *
	 * @param value Renavam as a String
	 * @return the new Renavam created
	 *
	 * @throws IllegalArgumentException if the specified String is not a valid Brazilian Renavam
	 */
	public static Renavam of(String value)
	{
		return new Renavam(Objects.requireNonNull(Renavam.format(value),
			"null is not a valid Renavam value"));
	}

	/**
	 * Checks if the specified value is a valid Brazilian Renavam
	 *
	 * @param value the value to be checked
	 * @return true, only if the specified value is a valid Brazilian Renavam
	 */
	public static boolean validate(String value)
	{
		if (value != null)
			if (value.length() >= 2
				&& value.charAt(value.length() - 2) == '-')
				switch (value.length())
				{
					case 12:
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
							value.charAt(11));

					case 11:
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
							value.charAt(10));

					case 10:
						return validate('0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6),
							value.charAt(7),
							value.charAt(9));
					case 9:
						return validate('0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6),
							value.charAt(8));

					case 8:
						return validate('0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(7));
					case 7:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(6));

					case 6:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(5));
					case 5:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(4));
					case 4:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(3));

					case 3:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(2));
				}
			else
				switch (value.length())
				{
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
						return validate('0',
							'0',
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
						return validate('0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6),
							value.charAt(7));
					case 7:
						return validate('0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6));

					case 6:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5));
					case 5:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4));

					case 4:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3));

					case 3:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2));

					case 2:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0),
							value.charAt(1));

					case 1:
						return validate('0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							'0',
							value.charAt(0));
				}

		return false;

	}

	/**
	 * Checks if the specified value is a valid Brazilian Renavam and formats it
	 *
	 * @param value the value to be formatted
	 * @return the specified value as a formatted Renavam or null if the specified value is not a valid Renavam
	 */
	public static String format(String value)
	{
		if (Renavam.validate(value))
			if (value.length() > 2 && value.charAt(value.length() - 2) == '-')
				switch (value.length())
				{
					case 12:
						return value;
					case 11:
						return '0' + value;
					case 10:
						return "00" + value;
					case 9:
						return "000" + value;
					case 8:
						return "0000" + value;
					case 7:
						return "00000" + value;
					case 6:
						return "000000" + value;
					case 5:
						return "0000000" + value;
					case 4:
						return "00000000" + value;
					case 3:
						return "000000000" + value;
				}
			else
				switch (value.length())
				{
					case 11:
						return value.substring(0, 10) + '-' + value.substring(10);
					case 10:
						return '0' + value.substring(0, 9) + '-' + value.substring(9);
					case 9:
						return "00" + value.substring(0, 8) + '-' + value.substring(8);
					case 8:
						return "000" + value.substring(0, 7) + '-' + value.substring(7);
					case 7:
						return "0000" + value.substring(0, 6) + '-' + value.substring(6);
					case 6:
						return "00000" + value.substring(0, 5) + '-' + value.substring(5);
					case 5:
						return "000000" + value.substring(0, 4) + '-' + value.substring(4);
					case 4:
						return "0000000" + value.substring(0, 3) + '-' + value.substring(3);
					case 3:
						return "00000000" + value.substring(0, 2) + '-' + value.substring(2);
					case 2:
						return "000000000" + value.substring(0, 1) + '-' + value.substring(1);
					case 1:
						return "0000000000" + '-' + value;
				}
		return null;
	}

	private static boolean validate(int c1, int c2, int c3, int c4, int c5, int c6,
		int c7, int c8, int c9, int c10, int digito)
	{
		int soma = 0;
		soma += Character.digit(c10, 10) * 2;
		soma += Character.digit(c9, 10) * 3;
		soma += Character.digit(c8, 10) * 4;
		soma += Character.digit(c7, 10) * 5;
		soma += Character.digit(c6, 10) * 6;
		soma += Character.digit(c5, 10) * 7;
		soma += Character.digit(c4, 10) * 8;
		soma += Character.digit(c3, 10) * 9;

		soma += Character.digit(c2, 10) * 2;
		soma += Character.digit(c1, 10) * 3;

		soma = (11 - soma % 11);
		soma = soma >= 10 ? 0 : soma;
		return Character.digit(digito, 10) == soma;
	}

	/**
	 * Returns the Renavam as a ###.###.###-## formatted String.
	 *
	 * @return the Renavam as a ###.###.###-## formatted String.
	 */
	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Renavam && ((Renavam) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value);
	}

	@Override
	public int compareTo(Renavam o)
	{
		return value.compareTo(o.value);
	}
}
