package gate.adapter.collector;

import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.EnumSet;

public class EnumSetCollector implements Collector
{
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object ofArray(Type type, Object[] array)
	{
		if (!(Reflection.getElementGenericType(type) instanceof Class<?> elementType)
				|| !Enum.class.isAssignableFrom(elementType))
			throw new ConversionException("EnumSet element type must be an enum.");

		var enumType = elementType.asSubclass(Enum.class);
		var collection = EnumSet.noneOf(enumType);
		if (array != null)
			for (Object element : array)
				collection.add(elementType.cast(element));
		return collection;
	}
}
