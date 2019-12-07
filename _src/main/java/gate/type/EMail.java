package gate.type;

import gate.annotation.Converter;
import gate.annotation.Icon;
import gate.converter.custom.EMailConverter;
import java.io.Serializable;

@Icon("2034")
@Converter(EMailConverter.class)
public class EMail implements Serializable
{

	private final String value;
	private static final long serialVersionUID = 1L;
	public static final String REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+([.]([a-zA-Z])+)+$";

	public EMail(String value)
	{
		if (value == null || !value.matches(REGEX))
			throw new IllegalArgumentException("value");
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof EMail && obj.toString().equals(toString());
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
