package gate.type;

import java.util.regex.Pattern;

public class MACAddress
{

	private final String value;
	private static final Pattern PATTERN = Pattern.compile("^[0-9a-fA-F]{12}$");

	private MACAddress(String value)
	{
		value = value.replaceAll("[\\-: .]", "");
		if (!PATTERN.matcher(value).matches())
			throw new IllegalArgumentException("value");

		this.value = value;
	}

	public static MACAddress valueOf(String value) {return new MACAddress(value);}

	@Override
	public String toString()
	{
		return String.format("%c%c%c%c.%c%c%c%c.%c%c%c%c", value.charAt(0),
				value.charAt(1), value.charAt(2), value.charAt(3),
				value.charAt(4), value.charAt(5), value.charAt(6), value.charAt(7),
				value.charAt(8), value.charAt(9), value.charAt(10), value.charAt(11));
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MACAddress && ((MACAddress) obj).value.equals(value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}