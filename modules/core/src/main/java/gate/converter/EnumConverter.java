package gate.converter;

import gate.annotation.Name;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.util.Collections;
import java.util.List;

public class EnumConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		if (Character.isDigit(string.charAt(0)))
			return type.getEnumConstants()[Integer.parseInt(string)];

		for (Object obj : type.getEnumConstants())
			if (((Enum<?>) obj).name().equals(string))
				return obj;
		throw new ConversionException("Invalid enum constant: " + string);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? String.valueOf(((Enum<?>) object).ordinal()) : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{

		return object != null
			? Name.Extractor.extract(object)
				.orElse(object.toString()) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, render(type, object)) : "";
	}

}
