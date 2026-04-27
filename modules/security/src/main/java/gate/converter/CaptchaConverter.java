package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.security.Captcha;

import java.util.Collections;
import java.util.List;

public class CaptchaConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string)
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

}