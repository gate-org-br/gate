package gate.adapter.converter.custom;

import gate.constraint.Constraint;
import gate.adapter.converter.Converter;
import gate.error.ConversionException;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeText;
import gate.util.Strings;

import java.util.Collections;
import java.util.List;

public class MimeDataFileConverter implements Converter
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
		return !Strings.empty(string)
				? MimeDataFile.parse(string) : null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((MimeText) object).getText() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, ((MimeText) object).getText()) : "";
	}

}