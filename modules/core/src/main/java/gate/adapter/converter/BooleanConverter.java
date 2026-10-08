package gate.adapter.converter;

import gate.constraint.Constraint;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class BooleanConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string)
	{
		return string != null && !string.trim().isEmpty() ? Boolean.valueOf(string) : null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}