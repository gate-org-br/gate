package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.Percentage;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class PercentageConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Number toNumber(Class<?> type, Object object)
	{
		return object != null ? ((Percentage) object).getValue() : null;
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.trim().isEmpty())
				return null;
			return Percentage.valueOf(string);
		} catch (ParseException e)
		{
			throw new ConversionException(String.format("%s não representa uma quantia válida.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : null;
	}

}