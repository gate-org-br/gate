package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.util.Reflection;

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
	@SuppressWarnings("unchecked")
	public Object ofString(Type type, String string)
	{
		return Encoder.of((Class<Object>) Reflection.getRawType(type))
				.decode(string);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString(Class<?> type, Object object)
	{
		return Encoder.of((Class<Object>) type).encode(object);
	}
}