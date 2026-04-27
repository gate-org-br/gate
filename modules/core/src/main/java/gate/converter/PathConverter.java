package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Description("Arquivo")
public class PathConverter implements Converter
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
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		return string != null && !string.isBlank() ? Path.of(string) : null;
	}
}
