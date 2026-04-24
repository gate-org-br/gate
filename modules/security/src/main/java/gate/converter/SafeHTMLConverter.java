package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.SafeHTML;

import java.util.List;

public class SafeHTMLConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "HTML";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
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
	public Object ofString(Class<?> type, String string) throws ConversionException
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