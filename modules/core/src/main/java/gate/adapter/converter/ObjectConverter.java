package gate.adapter.converter;

import gate.constraint.Constraint;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ObjectConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string)
	{
		return Encoder.of(type)
				.decode(string);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return Encoder.of(type).encode(object);
	}
}