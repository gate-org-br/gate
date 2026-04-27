package gate.converter.custom;
import gate.annotation.Description;

import java.util.List;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.lang.expression.Expression;

@Description("Valid expression")
public class ExpressionConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null || string.trim().isEmpty())
				return null;
			return Expression.of(string);
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

	@Override
	public String render(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return object.toString();
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		if (object == null)
			return "";
		return String.format(format, object);
	}

}
