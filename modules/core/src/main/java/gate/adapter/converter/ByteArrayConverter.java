package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Description("BLOB")
public class ByteArrayConverter implements Converter
{
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
	public Object ofString(Type type, String string)
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return Base64.getDecoder().decode(string);
	}

}