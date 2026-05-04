package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Description("Arquivo temporário")
public class TempFileConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		throw new UnsupportedOperationException(
				"Temporary files can't be represented as a string.");
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		throw new UnsupportedOperationException("Temporary files can't be represented as string.");
	}
}