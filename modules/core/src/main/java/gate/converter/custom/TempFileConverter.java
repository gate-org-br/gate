package gate.converter.custom;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.converter.Converter;
import gate.error.ConversionException;
import java.util.Collections;
import java.util.List;

@Description("Arquivo temporário")
public class TempFileConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object.toString()) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		throw new UnsupportedOperationException(
			"Temporary files can't be represented as a string.");
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		throw new UnsupportedOperationException("Temporary files can't be represented as string.");
	}
}
