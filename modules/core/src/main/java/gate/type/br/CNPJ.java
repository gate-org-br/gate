package gate.type.br;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Brazilian CNPJ value object backed by its 14 digits packed into a {@code long}.
 */
public record CNPJ(long value) implements Comparable<CNPJ>, BrasilianDocument, Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	public static final Pattern FORMATTED = Pattern.compile("^[0-9]{2}[.][0-9]{3}[.][0-9]{3}/[0-9]{4}-[0-9]{2}$");

	/**
	 * Creates a CNPJ from its numeric value.
	 *
	 * @param value the 14-digit CNPJ value
	 * @throws IllegalArgumentException if the value is not a valid CNPJ
	 */
	public CNPJ
	{
		if (!validate(value))
			throw new IllegalArgumentException(value + " is not a valid CNPJ value");
	}

	/**
	 * Returns this CNPJ in {@code ##.###.###/####-##} format.
	 */
	@Override
	public String toString()
	{
		return format(value);
	}

	/**
	 * Compares two CNPJ values by their numeric representation.
	 *
	 * @param other the CNPJ to compare to
	 * @return a negative number, zero, or a positive number as this CNPJ is less
	 * than, equal to, or greater than {@code other}
	 */
	@Override
	public int compareTo(CNPJ other)
	{
		return Long.compare(value, other.value);
	}

	/**
	 * Parses a raw or formatted CNPJ into its numeric representation.
	 *
	 * @param raw the CNPJ in {@code 99999999999999} or {@code 99.999.999/9999-99} format
	 * @return the parsed CNPJ value, or {@code -1} if the input is invalid
	 */
	public static long toLong(String raw)
	{
		if (raw == null)
			return -1;

		int d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13;
		char c;
		switch (raw.length())
		{
			case 18:
				if (raw.charAt(2) != '.' || raw.charAt(6) != '.' || raw.charAt(10) != '/' || raw.charAt(15) != '-')
					return -1;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d0 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d1 = c - '0';
				c = raw.charAt(3);
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
				c = raw.charAt(7);
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
				c = raw.charAt(11);
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
				c = raw.charAt(14);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(16);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(17);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 14:
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
				c = raw.charAt(11);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(12);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(13);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 13:
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
				c = raw.charAt(10);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(11);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(12);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 12:
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
				c = raw.charAt(9);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(10);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(11);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 11:
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
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(9);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(10);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 10:
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
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(9);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 9:
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
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(8);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 8:
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
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(7);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 7:
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
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(6);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 6:
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
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(5);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 5:
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
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(4);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			case 4:
				d0 = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;
				d4 = 0;
				d5 = 0;
				d6 = 0;
				d7 = 0;
				d8 = 0;
				d9 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d10 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(3);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
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
				d8 = 0;
				d9 = 0;
				d10 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d11 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(2);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
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
				d9 = 0;
				d10 = 0;
				d11 = 0;
				c = raw.charAt(0);
				if (c < '0' || c > '9')
					return -1;
				d12 = c - '0';
				c = raw.charAt(1);
				if (c < '0' || c > '9')
					return -1;
				d13 = c - '0';
				break;
			default:
				return -1;
		}

		int r1 = (d0 * 5 + d1 * 4 + d2 * 3 + d3 * 2 + d4 * 9 + d5 * 8 + d6 * 7 + d7 * 6 + d8 * 5 + d9 * 4 + d10 * 3 + d11 * 2) % 11;
		if ((r1 < 2 ? 0 : 11 - r1) != d12)
			return -1;

		int r2 = (d0 * 6 + d1 * 5 + d2 * 4 + d3 * 3 + d4 * 2 + d5 * 9 + d6 * 8 + d7 * 7 + d8 * 6 + d9 * 5 + d10 * 4 + d11 * 3 + d12 * 2) % 11;
		if ((r2 < 2 ? 0 : 11 - r2) != d13)
			return -1;

		return (long) d0 * 10_000_000_000_000L
		       + (long) d1 * 1_000_000_000_000L
		       + (long) d2 * 100_000_000_000L
		       + (long) d3 * 10_000_000_000L
		       + (long) d4 * 1_000_000_000L
		       + d5 * 100_000_000L
		       + d6 * 10_000_000L
		       + d7 * 1_000_000L
		       + d8 * 100_000L
		       + d9 * 10_000L
		       + d10 * 1_000L
		       + d11 * 100L
		       + d12 * 10L
		       + d13;
	}

	/**
	 * Creates a CNPJ from its numeric representation.
	 *
	 * @param value the 14-digit CNPJ value
	 * @return the created CNPJ
	 * @throws IllegalArgumentException if the value is not a valid CNPJ
	 */
	public static CNPJ valueOf(long value)
	{
		return new CNPJ(value);
	}

	/**
	 * Creates a CNPJ from a raw or formatted string.
	 *
	 * @param value the CNPJ in {@code 99999999999999} or {@code 99.999.999/9999-99} format
	 * @return the created CNPJ
	 * @throws IllegalArgumentException if the input is invalid
	 */
	public static CNPJ valueOf(String value)
	{
		long parsed = toLong(value);
		if (parsed < 0)
			throw new IllegalArgumentException(value + " is not a valid CNPJ value");
		return new CNPJ(parsed);
	}

	/**
	 * Checks whether a raw or formatted string is a valid CNPJ.
	 *
	 * @param value the CNPJ to validate
	 * @return {@code true} if the input is a valid CNPJ; {@code false} otherwise
	 */
	public static boolean validate(String value)
	{
		return toLong(value) >= 0;
	}

	/**
	 * Checks whether a numeric value is a valid CNPJ.
	 *
	 * @param value the CNPJ numeric value
	 * @return {@code true} if the value is a valid CNPJ; {@code false} otherwise
	 */
	public static boolean validate(long value)
	{
		if (value < 0 || value > 99_999_999_999_999L)
			return false;

		int d13 = (int) (value % 10);
		value /= 10;
		int d12 = (int) (value % 10);
		value /= 10;
		int d11 = (int) (value % 10);
		value /= 10;
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

		int r1 = (d0 * 5 + d1 * 4 + d2 * 3 + d3 * 2 + d4 * 9 + d5 * 8 + d6 * 7 + d7 * 6 + d8 * 5 + d9 * 4 + d10 * 3 + d11 * 2) % 11;
		if ((r1 < 2 ? 0 : 11 - r1) != d12)
			return false;

		int r2 = (d0 * 6 + d1 * 5 + d2 * 4 + d3 * 3 + d4 * 2 + d5 * 9 + d6 * 8 + d7 * 7 + d8 * 6 + d9 * 5 + d10 * 4 + d11 * 3 + d12 * 2) % 11;
		return (r2 < 2 ? 0 : 11 - r2) == d13;
	}

	/**
	 * Normalizes a raw or formatted CNPJ string to {@code ##.###.###/####-##}.
	 *
	 * @param value the CNPJ to format
	 * @return the formatted CNPJ, or {@code null} if the input is invalid
	 */
	public static String format(String value)
	{
		long parsed = toLong(value);
		return parsed < 0 ? null : format(parsed);
	}

	/**
	 * Formats a raw or formatted CNPJ as a 14-digit string with left zero padding.
	 *
	 * @param value the CNPJ numeric value
	 * @return the 14-digit CNPJ string, or {@code null} if the value is invalid
	 */
	public static String digits(String value)
	{
		long parsed = toLong(value);
		return parsed < 0 ? null : digits(parsed);
	}

	/**
	 * Formats a numeric CNPJ as {@code ##.###.###/####-##}.
	 *
	 * @param value the CNPJ numeric value
	 * @return the formatted CNPJ, or {@code null} if the value is invalid
	 */
	public static String format(long value)
	{
		if (!validate(value))
			return null;

		char[] chars = new char[18];
		chars[2] = '.';
		chars[6] = '.';
		chars[10] = '/';
		chars[15] = '-';

		chars[17] = (char) ('0' + (value % 10));
		value /= 10;
		chars[16] = (char) ('0' + (value % 10));
		value /= 10;
		chars[14] = (char) ('0' + (value % 10));
		value /= 10;
		chars[13] = (char) ('0' + (value % 10));
		value /= 10;
		chars[12] = (char) ('0' + (value % 10));
		value /= 10;
		chars[11] = (char) ('0' + (value % 10));
		value /= 10;
		chars[9] = (char) ('0' + (value % 10));
		value /= 10;
		chars[8] = (char) ('0' + (value % 10));
		value /= 10;
		chars[7] = (char) ('0' + (value % 10));
		value /= 10;
		chars[5] = (char) ('0' + (value % 10));
		value /= 10;
		chars[4] = (char) ('0' + (value % 10));
		value /= 10;
		chars[3] = (char) ('0' + (value % 10));
		value /= 10;
		chars[1] = (char) ('0' + (value % 10));
		value /= 10;
		chars[0] = (char) ('0' + value);
		return new String(chars);
	}

	/**
	 * Formats a numeric CNPJ as a 14-digit string with left zero padding.
	 *
	 * @param value the CNPJ numeric value
	 * @return the 14-digit CNPJ string, or {@code null} if the value is invalid
	 */
	public static String digits(long value)
	{
		if (!validate(value))
			return null;

		char[] chars = new char[14];
		chars[13] = (char) ('0' + (value % 10));
		value /= 10;
		chars[12] = (char) ('0' + (value % 10));
		value /= 10;
		chars[11] = (char) ('0' + (value % 10));
		value /= 10;
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