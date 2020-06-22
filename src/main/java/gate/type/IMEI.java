package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.IMEIConverter;
import java.io.Serializable;

@Converter(IMEIConverter.class)
public class IMEI implements Serializable, Comparable<IMEI>
{

	private static final long serialVersionUID = 1L;

	private final String value;

	public IMEI(String value)
	{
		value = value.replaceAll("[^0123456789]", "");
		if (value.length() != 15)
			throw new IllegalArgumentException("value");

		int[] digits = new int[value.length()];
		for (int i = 0; i < value.length(); i++)
			digits[i] = Character.digit(value.charAt(i), 10);
		for (int i = 1; i <= 13; i += 2)
			digits[i] = digits[i] * 2;

		int sum = 0;
		for (int i = 0; i < digits.length; i++)
		{
			char[] chars = String.valueOf(digits[i]).toCharArray();
			for (int j = 0; j < chars.length; j++)
				sum += Character.digit(chars[j], 10);
		}

		if (sum % 10 != 0)
			throw new IllegalArgumentException("value");

		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof IMEI && ((IMEI) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));
	}

	@Override
	public String toString()
	{
		return String.format("%c%c-%c%c%c%c%c%c-%c%c%c%c%c%c-%c", value.charAt(0),
			value.charAt(1), value.charAt(2), value.charAt(3),
			value.charAt(4), value.charAt(5), value.charAt(6),
			value.charAt(7), value.charAt(8), value.charAt(9),
			value.charAt(10), value.charAt(11), value.charAt(12),
			value.charAt(13), value.charAt(14));
	}

	@Override
	public int compareTo(IMEI o)
	{
		return value.compareTo(o.value);
	}
}
