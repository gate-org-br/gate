package gate.converter.custom;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.mime.MimeText;
import gate.type.mime.MimeTextFile;
import gate.util.Strings;
import java.util.Collections;
import java.util.List;

public class MimeTextFileConverter implements Converter
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
	public Object ofString(Class<?> type, String string)
			throws ConversionException
	{
		return !Strings.empty(string)
				? MimeTextFile.parse(string) : null;
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
