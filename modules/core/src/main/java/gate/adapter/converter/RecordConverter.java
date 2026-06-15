package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RecordConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		return Encoder.of(type).decode(string);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return Encoder.of(type).encode(object);
	}
}