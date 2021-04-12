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
			switch (value.length())
			{
				case 12:
					return value.charAt(10) == '-'
						? validate(value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6),
							value.charAt(7),
							value.charAt(8),
							value.charAt(9),
							value.charAt(11)) : false;

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
					return value.charAt(8) == '-'
						? validate('0',
							'0',
							value.charAt(0),
							value.charAt(1),
							value.charAt(2),
							value.charAt(3),
							value.charAt(4),
							value.charAt(5),
							value.charAt(6),
							value.charAt(7),
							value.charAt(9)) : false;

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
			switch (value.length())
			{
				case 12:
					return value;
				case 11:
					return value.substring(0, 10) + '-' + value.substring(10);
				case 10:
					return "00" + value;
				case 9:
					return "00" + value.substring(0, 8) + '-' + value.substring(8);
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
