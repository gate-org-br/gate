package gate.type.br;

import gate.annotation.Converter;
import gate.converter.custom.CPFConverter;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Brazilian CPF value object backed by its 11 digits packed into a {@code long}.
 */
@Converter(CPFConverter.class)
public record CPF(long value) implements Comparable<CPF>, BrasilianDocument, Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	public static final Pattern RAW = Pattern.compile("^[0-9]{11}$");
	public static final Pattern FORMATTED = Pattern.compile("^[0-9]{3}[.][0-9]{3}[.][0-9]{3}[-][0-9]{2}$");

	/**
	 * Creates a CPF from its numeric value.
	 *
	 * @param value the 11-digit CPF value
	 * @throws IllegalArgumentException if the value is not a valid CPF
	 */
	public CPF
	{
		if (!validate(value))
			throw new IllegalArgumentException(value + " is not a valid CPF value");
	}

	/**
	 * Returns this CPF in {@code ###.###.###-##} format.
	 */
	@Override
	public String toString()
	{
		return format(value);
	}

	/**
	 * Compares two CPF values by their numeric representation.
	 *
	 * @param other the CPF to compare to
	 * @return a negative number, zero, or a positive number as this CPF is less
	 * than, equal to, or greater than {@code other}
	 */
	@Override
	public int compareTo(CPF other)
	{
		return Long.compare(value, other.value);
	}

	/**
	 * Parses a raw or formatted CPF into its numeric representation.
	 *
	 * @param raw the CPF in {@code 99999999999} or {@code 999.999.999-99} format
	 * @return the parsed CPF value, or {@code -1} if the input is invalid
	 */
	public static long toLong(String raw)
	{
		if (raw == null)
			return -1;

		int d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10;
		char c;
		switch (raw.length())
		{
			case 14:
				if (raw.charAt(3) != '.' || raw.charAt(7) != '.' || raw.charAt(11) != '-')
					return -1;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d0 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d1 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d2 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d3 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d4 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(9);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(10);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(12);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(13);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 11:
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d0 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d1 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d2 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d3 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d4 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(9);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(10);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 10:
				d0 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d1 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d2 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d3 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d4 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(9);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 9:
				d0 = 0;
				d1 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d2 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d3 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d4 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 8:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d3 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d4 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 7:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d4 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 6:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				d4 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d5 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 5:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				d4 = 0;
				d5 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d6 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 4:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				d4 = 0;
				d5 = 0;
				d6 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d7 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 3:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				d4 = 0;
				d5 = 0;
				d6 = 0;
				d7 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d8 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			case 2:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				d4 = 0;
				d5 = 0;
				d6 = 0;
				d7 = 0;
				d8 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d9 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				break;
			default:
				return -1;
		}

		int r1 = (d0 * 10 + d1 * 9 + d2 * 8 + d3 * 7 + d4 * 6 + d5 * 5 + d6 * 4 + d7 * 3 + d8 * 2) % 11;
		if ((r1 < 2 ? 0 : 11 - r1) != d9)
			return -1;

		int r2 = (d0 * 11 + d1 * 10 + d2 * 9 + d3 * 8 + d4 * 7 + d5 * 6 + d6 * 5 + d7 * 4 + d8 * 3 + d9 * 2) % 11;
		if ((r2 < 2 ? 0 : 11 - r2) != d10)
			return -1;

		return (long) d0 * 10_000_000_000L
			   + (long) d1 * 1_000_000_000L
			   + d2 * 100_000_000
			   + d3 * 10_000_000
			   + d4 * 1_000_000
			   + d5 * 100_000
			   + d6 * 10_000
			   + d7 * 1_000
			   + d8 * 100
			   + d9 * 10
			   + d10;
	}

	/**
	 * Creates a CPF from its numeric representation.
	 *
	 * @param value the 11-digit CPF value
	 * @return the created CPF
	 * @throws IllegalArgumentException if the value is not a valid CPF
	 */
	public static CPF of(long value)
	{
		return new CPF(value);
	}

	/**
	 * Creates a CPF from a raw or formatted string.
	 *
	 * @param value the CPF in {@code 99999999999} or {@code 999.999.999-99} format
	 * @return the created CPF
	 * @throws IllegalArgumentException if the input is invalid
	 */
	public static CPF of(String value)
	{
		long parsed = toLong(value);
		if (parsed < 0)
			throw new IllegalArgumentException(value + " is not a valid CPF value");
		return new CPF(parsed);
	}

	/**
	 * Checks whether a raw or formatted string is a valid CPF.
	 *
	 * @param value the CPF to validate
	 * @return {@code true} if the input is a valid CPF; {@code false} otherwise
	 */
	public static boolean validate(String value)
	{
		return toLong(value) >= 0;
	}

	/**
	 * Checks whether a numeric value is a valid CPF.
	 *
	 * @param value the CPF numeric value
	 * @return {@code true} if the value is a valid CPF; {@code false} otherwise
	 */
	public static boolean validate(long value)
	{
		if (value < 0 || value > 99_999_999_999L)
			return false;

		int d10 = (int) (value % 10);
		value /= 10;
		int d9 = (int) (value % 10);
		value /= 10;
		int d8 = (int) (value % 10);
		value /= 10;
		int d7 = (int) (value % 10);
		value /= 10;
		int d6 = (int) (value % 10);
		value /= 10;
		int d5 = (int) (value % 10);
		value /= 10;
		int d4 = (int) (value % 10);
		value /= 10;
		int d3 = (int) (value % 10);
		value /= 10;
		int d2 = (int) (value % 10);
		value /= 10;
		int d1 = (int) (value % 10);
		int d0 = (int) (value / 10);

		int r1 = (d0 * 10 + d1 * 9 + d2 * 8 + d3 * 7 + d4 * 6 + d5 * 5 + d6 * 4 + d7 * 3 + d8 * 2) % 11;
		if ((r1 < 2 ? 0 : 11 - r1) != d9)
			return false;

		int r2 = (d0 * 11 + d1 * 10 + d2 * 9 + d3 * 8 + d4 * 7 + d5 * 6 + d6 * 5 + d7 * 4 + d8 * 3 + d9 * 2) % 11;
		return (r2 < 2 ? 0 : 11 - r2) == d10;
	}

	/**
	 * Normalizes a raw or formatted CPF string to {@code ###.###.###-##}.
	 *
	 * @param value the CPF to format
	 * @return the formatted CPF, or {@code null} if the input is invalid
	 */
	public static String format(String value)
	{
		long parsed = toLong(value);
		return parsed < 0 ? null : format(parsed);
	}

	/**
	 * Formats a numeric CPF as {@code ###.###.###-##}.
	 *
	 * @param value the CPF numeric value
	 * @return the formatted CPF, or {@code null} if the value is invalid
	 */
	public static String format(long value)
	{
		if (!validate(value))
			return null;

		char[] chars = new char[14];
		chars[3] = '.';
		chars[7] = '.';
		chars[11] = '-';

		chars[13] = (char) ('0' + (value % 10));
		value /= 10;
		chars[12] = (char) ('0' + (value % 10));
		value /= 10;
		chars[10] = (char) ('0' + (value % 10));
		value /= 10;
		chars[9] = (char) ('0' + (value % 10));
		value /= 10;
		chars[8] = (char) ('0' + (value % 10));
		value /= 10;
		chars[6] = (char) ('0' + (value % 10));
		value /= 10;
		chars[5] = (char) ('0' + (value % 10));
		value /= 10;
		chars[4] = (char) ('0' + (value % 10));
		value /= 10;
		chars[2] = (char) ('0' + (value % 10));
		value /= 10;
		chars[1] = (char) ('0' + (value % 10));
		value /= 10;
		chars[0] = (char) ('0' + value);
		return new String(chars);
	}

	/**
	 * Formats a numeric CPF as an 11-digit string with left zero padding.
	 *
	 * @param value the CPF numeric value
	 * @return the 11-digit CPF string, or {@code null} if the value is invalid
	 */
	public static String digits(long value)
	{
		if (!validate(value))
			return null;

		char[] chars = new char[11];
		chars[10] = (char) ('0' + (value % 10));
		value /= 10;
		chars[9] = (char) ('0' + (value % 10));
		value /= 10;
		chars[8] = (char) ('0' + (value % 10));
		value /= 10;
		chars[7] = (char) ('0' + (value % 10));
		value /= 10;
		chars[6] = (char) ('0' + (value % 10));
		value /= 10;
		chars[5] = (char) ('0' + (value % 10));
		value /= 10;
		chars[4] = (char) ('0' + (value % 10));
		value /= 10;
		chars[3] = (char) ('0' + (value % 10));
		value /= 10;
		chars[2] = (char) ('0' + (value % 10));
		value /= 10;
		chars[1] = (char) ('0' + (value % 10));
		value /= 10;
		chars[0] = (char) ('0' + value);
		return new String(chars);
	}
}