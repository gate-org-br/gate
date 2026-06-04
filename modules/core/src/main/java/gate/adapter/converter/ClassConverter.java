package gate.adapter.converter;

import gate.constraint.Constraint;


import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ClassConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string)
	{
		try
		{
			return string != null && !string.trim().isEmpty() ? Thread.currentThread()
																.getContextClassLoader()
																.loadClass(string) : null;
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Class<?>) object).getName() : "";
	}

}