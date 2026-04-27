package gate.converter;

import gate.constraint.Constraint;
import gate.error.AppError;

import java.util.Collections;
import java.util.List;

public class ClassConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		try
		{
			return string != null && !string.trim().isEmpty() ? Thread.currentThread().getContextClassLoader().loadClass(string) : null;
		} catch (ClassNotFoundException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Class<?>) object).getName() : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((Class<?>) object).getName() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, render(type, object)) : "";
	}

}
