package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.SafeName;

import java.util.List;

@Description("Use apenas letras, números, espaços ou hífen.")
public class SafeNameConverter implements Converter
{

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;

			string = string.trim();
			if (string.isEmpty())
				return null;

			return SafeName.valueOf(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(ex, ex.getMessage());
		}
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of(new Pattern.Implementation(SafeName.PATTERN.pattern()));
	}

}