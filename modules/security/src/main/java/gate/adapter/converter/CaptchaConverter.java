package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.security.Captcha;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CaptchaConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string)
			throws ConversionException
	{
		if (string != null && string.trim().length() > 0)
			return Captcha.of(string);
		return null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object == null ? "" : object.toString();
	}

	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

}