package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.ProcessNumberConverter;
import java.io.Serializable;
import java.math.BigDecimal;

@Converter(ProcessNumberConverter.class)
public class ProcessNumber implements Serializable, Cloneable
{

	private static final long serialVersionUID = 1L;

	private String value;

	public ProcessNumber(String value)
	{
		if (value.matches(
			"^[0-9]{4}[.][0-9]{2}[.][0-9]{2}[.][0-9]{6}[-][0-9]|[0-9]{2}[.][0-9]{2}.[0-9]{5}[-][0-9]|[0-9]{7}-[0-9]{2}[.][0-9]{4}.[0-9].[0-9]{2}.[0-9]{4}$"))
			value = value.replaceAll("[^0123456789]", "");
		if (!value.matches("[0-9]{10}|[0-9]{15}|[0-9]{20}"))
			throw new IllegalArgumentException("value");

		if (value.length() == 10)
		{
			int soma = 0;
			for (int i = 0; i <= 8; i++)
				soma += (i + 1) * Integer.parseInt(String.valueOf(value.charAt(i)));
			if (soma % 11 % 10 != Integer.parseInt(String.valueOf(value.charAt(9))))
				throw new IllegalArgumentException("value");
		} else if (value.length() == 15 && Integer.parseInt(value.substring(0, 4)) <= 2001)
		{
			int soma = 0;
			for (int i = 0; i <= 13; i++)
				soma += (i + 1) * Integer.parseInt(String.valueOf(value.charAt(i)));
			if (soma % 11 % 10 != Integer.parseInt(String.valueOf(value.charAt(14))))
				throw new IllegalArgumentException("value");
		} else if (value.length() == 15 && Integer.parseInt(value.substring(0, 4)) >= 2002)
		{
			int soma = 0;
			for (int i = 0; i <= 13; i++)
				soma += ((i % 10) + 1) * Integer.parseInt(String.valueOf(value.charAt(i)));
			if (soma % 11 % 10 != Integer.parseInt(String.valueOf(value.charAt(14))))
				throw new IllegalArgumentException("value");
		} else if (!new BigDecimal(98).subtract(new BigDecimal(String.format("%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c00", value.charAt(0), value.charAt(1), value
			.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5), value.charAt(6), value.charAt(9), value.charAt(10), value.charAt(11), value
			.charAt(12), value.charAt(13), value.charAt(14), value.charAt(15), value.charAt(16), value.charAt(17), value.charAt(18), value.charAt(19)))
			.remainder(new BigDecimal(97))).equals(new BigDecimal(String.format("%c%c", value.charAt(7), value.charAt(8)))))
			throw new IllegalArgumentException("value");

		this.value = value;
	}

	@Override
	protected ProcessNumber clone()
	{
		return new ProcessNumber(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof ProcessNumber && ((ProcessNumber) obj).value.equals(value));
	}

	@Override
	public int hashCode()
	{
		return Integer.parseInt(value.replaceAll("[^0123456789]", ""));
	}

	@Override
	public String toString()
	{
		if (value.matches("^[0-9]{10}$"))
			return String
				.format("%c%c.%c%c.%c%c%c%c%c-%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4), value.charAt(5),
					value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9));

		if (value.matches("^[0-9]{15}$"))
			return String.format("%c%c%c%c.%c%c.%c%c.%c%c%c%c%c%c-%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value.charAt(4),
				value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value.charAt(12),
				value.charAt(13), value.charAt(14));

		if (value.matches("^[0-9]{20}$"))
			return String.format("%c%c%c%c%c%c%c-%c%c.%c%c%c%c.%c.%c%c.%c%c%c%c", value.charAt(0), value.charAt(1), value.charAt(2), value.charAt(3), value
				.charAt(4), value.charAt(5), value.charAt(6), value.charAt(7), value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11), value
				.charAt(12), value.charAt(13), value.charAt(14), value.charAt(15), value.charAt(16), value.charAt(17), value.charAt(18), value.charAt(19));

		return value;
	}

}
