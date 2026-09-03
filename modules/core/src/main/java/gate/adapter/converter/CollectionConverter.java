package gate.adapter.converter;

import gate.adapter.collector.Collector;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints() {return Collections.emptyList();}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (!(object instanceof Collection<?> collection))
			return "";

		return collection.stream()
				.filter(Objects::nonNull)
				.map(Converter::toString)
				.collect(Collectors.joining("\n"));
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		Class<?> rawType = Reflection.getRawType(type);
		if (rawType == null || !Collection.class.isAssignableFrom(rawType))
			throw new ConversionException("Type %s is not a collection.".formatted(type));

		Type elementType = Objects.requireNonNullElse(Reflection.getElementGenericType(type), String.class);
		var values = Arrays.stream(string.split("\\s*(?:[,;]|\\r\\n|\\n|\\r)\\s*"))
				.filter(item -> !item.isBlank())
				.map(item -> Converter.fromString(elementType, item))
				.toArray();
		return Collector.fromArray(type, values);
	}
}