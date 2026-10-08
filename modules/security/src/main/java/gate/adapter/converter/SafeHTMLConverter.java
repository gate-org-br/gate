package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.SafeHTML;

import java.lang.reflect.Type;
import java.util.List;

@Description("HTML")
public class SafeHTMLConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
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

		try
		{
			return SafeHTML.of(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(string.concat(" não é um HTML válido."));
		}
	}

}