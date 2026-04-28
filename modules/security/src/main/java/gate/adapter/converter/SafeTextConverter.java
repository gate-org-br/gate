package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.SafeText;

import java.util.List;

@Description("Use apenas letras, números, espaços, quebras de linha e pontuação simples.")
public class SafeTextConverter implements Converter
{
	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;

			if (string.isBlank())
				return null;

			return SafeText.valueOf(string);
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
		return List.of(new Pattern.Implementation(SafeText.PATTERN.pattern()));
	}

}