package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.i18n.CurrentLocale;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class FloatConverter implements Converter
{
	private static final java.util.regex.Pattern ISO_PATTERN
			= java.util.regex.Pattern.compile("[+-]?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");
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
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;

			string = string.trim();
			if (string.isEmpty())
				return null;

			return ISO_PATTERN.matcher(string).matches()
					? Float.valueOf(string)
					: Float.valueOf(CurrentLocale.getDecimalFormat().parse(string).floatValue());
		} catch (NumberFormatException | ParseException e)
		{
			throw new ConversionException(String.format("%s não é um decimal válido.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? CurrentLocale.getDecimalFormat().format(object) : "";
	}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}