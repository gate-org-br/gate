package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.type.mime.MimeText;
import gate.util.Strings;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MimeTextConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string)
			throws ConversionException
	{
		return !Strings.empty(string)
				? MimeText.valueOF(string) : null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

}