package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.PhoneConverter;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Icon("2223")
@Converter(PhoneConverter.class)
public class Phone implements Serializable, Comparable<Phone>
{

	private static final long serialVersionUID = 1L;

	private final String value;
	private static final Pattern PATTERN = Pattern.compile("^0*([1-9][1-9])??([1-9][1-9])?(9?[0-9]{8})$");

	public Phone(String value)
	{
		if (value == null)
			throw new IllegalArgumentException("value can't be null");
		this.value = value.replaceAll("\\[|\\]|\\(|\\)|\\-|\\ |\\.", "");
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Phone && value.equals(((Phone) obj).value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public int compareTo(Phone o)
	{
		return value.compareTo(o.value);
	}

	public String getProvider()
	{
		Matcher matcher = PATTERN.matcher(value);
		if (matcher.matches())
			return matcher.group(1);
		return null;
	}

	public String getDDD()
	{
		Matcher matcher = PATTERN.matcher(value);
		if (matcher.matches())
			return matcher.group(2);
		return null;
	}

	public String getNumber()
	{
		Matcher matcher = PATTERN.matcher(value);
		if (matcher.matches())
			return matcher.group(3);
		return null;
	}
}
