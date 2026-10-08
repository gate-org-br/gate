package gate.type.br;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Brazilian CNPJ value object.
 * <p>
 * The first 12 positions may contain digits or uppercase letters.
 * The last two positions are numeric check digits.
 */
public record CNPJ(String value) implements Comparable<CNPJ>, BrasilianDocument, Serializable
{
	public static final Pattern FORMATTED = Pattern.compile("^[0-9]{2}[.][0-9]{3}[.][0-9]{3}/[0-9]{4}-[0-9]{2}$");

	@Serial
	private static final long serialVersionUID = 1L;

	public CNPJ
	{
		String original = value;
		value = format(value);

		if (value == null)
			throw new IllegalArgumentException(original + " is not a valid CNPJ value");
	}

	public static boolean validate(String value)
	{
		if (value == null)
			return false;

		value = value.trim()
				.toUpperCase(Locale.ROOT);

		if (value.chars().filter(Character::isDigit).distinct().count() < 2)
			return false;

		if (value.length() < 14)
			value = "0".repeat(14 - value.length()) + value;

		return switch (value.length())
		{
			case 14 ->
			{
				if (value.charAt(0) == value.charAt(1)
						&& value.charAt(0) == value.charAt(2)
						&& value.charAt(0) == value.charAt(3)
						&& value.charAt(0) == value.charAt(4)
						&& value.charAt(0) == value.charAt(5)
						&& value.charAt(0) == value.charAt(6)
						&& value.charAt(0) == value.charAt(7)
						&& value.charAt(0) == value.charAt(8)
						&& value.charAt(0) == value.charAt(9)
						&& value.charAt(0) == value.charAt(10)
						&& value.charAt(0) == value.charAt(11)
						&& value.charAt(0) == value.charAt(12)
						&& value.charAt(0) == value.charAt(13))
					yield false;

				for (int i = 0; i < 12; i++)
				{
					char character = value.charAt(i);

					if ((character < '0' || character > '9')
							&& (character < 'A' || character > 'Z'))
						yield false;
				}

				char firstDigit = value.charAt(12);
				char secondDigit = value.charAt(13);

				if (firstDigit < '0' || firstDigit > '9'
						|| secondDigit < '0' || secondDigit > '9')
					yield false;

				int sum = 0;
				int weight = 5;

				for (int i = 0; i < 12; i++)
				{
					sum += (value.charAt(i) - '0') * weight;

					if (--weight == 1)
						weight = 9;
				}

				int remainder = sum % 11;
				int digit = remainder < 2 ? 0 : 11 - remainder;

				if (digit != firstDigit - '0')
					yield false;

				sum = 0;
				weight = 6;

				for (int i = 0; i < 13; i++)
				{
					sum += (value.charAt(i) - '0') * weight;

					if (--weight == 1)
						weight = 9;
				}

				remainder = sum % 11;
				digit = remainder < 2 ? 0 : 11 - remainder;

				yield digit == secondDigit - '0';
			}

			case 18 ->
			{
				if (value.charAt(0) == value.charAt(1)
						&& value.charAt(0) == value.charAt(3)
						&& value.charAt(0) == value.charAt(4)
						&& value.charAt(0) == value.charAt(5)
						&& value.charAt(0) == value.charAt(7)
						&& value.charAt(0) == value.charAt(8)
						&& value.charAt(0) == value.charAt(9)
						&& value.charAt(0) == value.charAt(11)
						&& value.charAt(0) == value.charAt(12)
						&& value.charAt(0) == value.charAt(13)
						&& value.charAt(0) == value.charAt(14)
						&& value.charAt(0) == value.charAt(16)
						&& value.charAt(0) == value.charAt(17))
					yield false;

				if (value.charAt(2) != '.'
						|| value.charAt(6) != '.'
						|| value.charAt(10) != '/'
						|| value.charAt(15) != '-')
					yield false;

				for (int i = 0; i < 15; i++)
				{
					if (i == 2 || i == 6 || i == 10)
						continue;

					char character = value.charAt(i);
					if ((character < '0' || character > '9')
							&& (character < 'A' || character > 'Z'))
						yield false;
				}

				char firstDigit = value.charAt(16);
				char secondDigit = value.charAt(17);

				if (firstDigit < '0' || firstDigit > '9'
						|| secondDigit < '0' || secondDigit > '9')
					yield false;

				int sum = 0;
				int weight = 5;

				for (int i = 0; i < 15; i++)
				{
					if (i == 2 || i == 6 || i == 10)
						continue;

					sum += (value.charAt(i) - '0') * weight;

					if (--weight == 1)
						weight = 9;
				}

				int remainder = sum % 11;
				int digit = remainder < 2 ? 0 : 11 - remainder;

				if (digit != firstDigit - '0')
					yield false;

				sum = 0;
				weight = 6;

				for (int i = 0; i < 15; i++)
				{
					if (i == 2 || i == 6 || i == 10)
						continue;

					sum += (value.charAt(i) - '0') * weight;

					if (--weight == 1)
						weight = 9;
				}

				sum += (firstDigit - '0') * 2;

				remainder = sum % 11;
				digit = remainder < 2 ? 0 : 11 - remainder;

				yield digit == secondDigit - '0';
			}

			default -> false;
		};
	}

	public static String format(String value)
	{
		if (!validate(value))
			return null;

		value = value.trim()
				.toUpperCase(Locale.ROOT);

		if (value.length() == 18)
			return value;

		if (value.length() < 14)
			value = "0".repeat(14 - value.length()) + value;

		return value.substring(0, 2)
				+ "."
				+ value.substring(2, 5)
				+ "."
				+ value.substring(5, 8)
				+ "/"
				+ value.substring(8, 12)
				+ "-"
				+ value.substring(12, 14);
	}

	public static String raw(String value)
	{
		if (!validate(value))
			return null;

		value = value.trim()
				.toUpperCase(Locale.ROOT);

		if (value.length() == 14)
			return value;

		return value.substring(0, 2)
				+ value.substring(3, 6)
				+ value.substring(7, 10)
				+ value.substring(11, 15)
				+ value.substring(16, 18);
	}


	public static CNPJ valueOf(String value) {return new CNPJ(value);}

	@Override
	public int compareTo(CNPJ other) {return value.compareTo(other.value);}

	@Override
	public String toString() {return value;}
}