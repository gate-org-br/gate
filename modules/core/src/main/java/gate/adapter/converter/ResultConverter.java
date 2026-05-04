package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.Result;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ResultConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Number toNumber(Class<?> type, Object object)
	{
		return (Number) object;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		return Result.valueOf(string);
	}

}