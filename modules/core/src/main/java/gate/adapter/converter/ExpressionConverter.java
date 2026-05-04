package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.expression.Expression;

import java.lang.reflect.Type;
import java.util.List;

@Description("Valid expression")
public class ExpressionConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.trim().isEmpty())
				return null;
			return Expression.valueOf(string);
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(String.format("%s is not a valid expression.", string));
		}

	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return object.toString();
	}

}