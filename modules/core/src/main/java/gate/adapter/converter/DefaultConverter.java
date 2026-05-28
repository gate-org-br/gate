package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DefaultConverter implements Converter
{
	private final Method valueOf;

	public DefaultConverter(Method valueOf)
	{
		this.valueOf = valueOf;
	}

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return valueOf.invoke(null, string);
		} catch (IllegalAccessException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		} catch (InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			if (cause instanceof ConversionException conversionException)
				throw conversionException;
			throw new ConversionException(cause.getMessage(), cause);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}