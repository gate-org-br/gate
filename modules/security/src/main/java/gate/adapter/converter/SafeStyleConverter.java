package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.SafeStyle;

import java.lang.reflect.Type;
import java.util.List;

@Description("CSS inline seguro.")
public class SafeStyleConverter implements Converter
{
	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;

			string = string.trim();
			if (string.isEmpty())
				return null;

			return SafeStyle.of(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(ex, ex.getMessage());
		}
	}

	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

}