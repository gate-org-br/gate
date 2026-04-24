package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.util.Collections;
import java.util.List;

public class FileConverter implements Converter
{

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Arquivo";
	}

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
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object
	)
	{
		throw new UnsupportedOperationException("Files can't be represented as strings.");
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		throw new UnsupportedOperationException("Files can't be represented as strings.");
	}
}
