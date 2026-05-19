package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.i18n.CurrentLocale;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class ShortConverter implements Converter
{
	private static final java.util.regex.Pattern ISO_PATTERN = java.util.regex.Pattern.compile("[+-]?\\d+");

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
	public String toString(Class<?> type, Object object)
	{
		return object != null ? CurrentLocale.getIntegerFormat().format(object) : "";
	}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
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
					? Short.valueOf(string)
					: Short.valueOf(Long.toString(CurrentLocale.getIntegerFormat().parse(string).longValue()));
		} catch (NumberFormatException | ParseException ex)
		{
			throw new ConversionException(ex, "%s não é um inteiro válido.", string);
		}
	}
}