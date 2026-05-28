package gate.type.br;

import java.io.Serial;

import java.io.Serializable;

public class CEP implements Serializable
{


	@Serial
	private static final long serialVersionUID = 1L;

	private final String value;

	private CEP(String value) {this.value = value;}

	public static CEP valueOf(String string)
	{
		if (!validate(string))
			throw new IllegalArgumentException("value");
		return new CEP(digits(string));
	}

	public static String digits(String string)
	{
		if (!validate(string))
			throw new IllegalArgumentException("value");
		return string.chars()
				.filter(Character::isDigit)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	public static String format(String string)
	{
		if (!validate(string))
			throw new IllegalArgumentException("value");
		string = digits(string);
		return "%c%c.%c%c%c-%c%c%c".formatted(
				string.charAt(0),
				string.charAt(1),
				string.charAt(2),
				string.charAt(3),
				string.charAt(4),
				string.charAt(5),
				string.charAt(6),
				string.charAt(7));
	}

	public static boolean validate(String string)
	{
		return switch (string.length())
		{
			case 8 -> string.chars().allMatch(Character::isDigit);
			case 9 -> Character.isDigit(string.charAt(0))
			          && Character.isDigit(string.charAt(1))
			          && Character.isDigit(string.charAt(2))
			          && Character.isDigit(string.charAt(3))
			          && Character.isDigit(string.charAt(4))
			          && string.charAt(5) == '-'
			          && Character.isDigit(string.charAt(6))
			          && Character.isDigit(string.charAt(7))
			          && Character.isDigit(string.charAt(8));
			case 10 -> Character.isDigit(string.charAt(0))
			           && Character.isDigit(string.charAt(1))
			           && string.charAt(2) == '.'
			           && Character.isDigit(string.charAt(3))
			           && Character.isDigit(string.charAt(4))
			           && Character.isDigit(string.charAt(5))
			           && string.charAt(6) == '-'
			           && Character.isDigit(string.charAt(7))
			           && Character.isDigit(string.charAt(8))
			           && Character.isDigit(string.charAt(9));
			default -> false;
		};
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CEP && ((CEP) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));
	}

	@Override
	public String toString()
	{
		return String.format("%c%c.%c%c%c-%c%c%c",
				value.charAt(0),
				value.charAt(1),
				value.charAt(2),
				value.charAt(3),
				value.charAt(4),
				value.charAt(5),
				value.charAt(6),
				value.charAt(7));
	}
}