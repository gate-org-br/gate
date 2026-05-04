package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.i18n.CurrentLocale;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class BigDecimalConverter implements Converter
{
	private static final java.util.regex.Pattern ISO_PATTERN
			= java.util.regex.Pattern.compile("[+-]?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Collections.singletonList(new Pattern.Implementation("^[0-9]+([.][0-9]{1,2})?$"));

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return ISO_PATTERN.matcher(string).matches()
				? new BigDecimal(string)
				: new BigDecimal(CurrentLocale.parseBigDecimal(string).toString());
	}

	@Override
	public Number toNumber(Class<?> type, Object object)
	{
		return (BigDecimal) object;
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
