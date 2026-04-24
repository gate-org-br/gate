package gate.converter;

import gate.constraint.Constraint;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class ByteArrayConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "BLOB";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return Base64.getEncoder().encodeToString((byte[]) object);
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return Base64.getDecoder().decode(string);
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return toString(type, object);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format, toString(type, object));
	}

}
