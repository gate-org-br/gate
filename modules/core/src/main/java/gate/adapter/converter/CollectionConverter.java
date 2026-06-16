package gate.adapter.converter;

import gate.util.Instance;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CollectionConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

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
	@SuppressWarnings("unchecked")
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

		Type elementType = getElementType(type, rawType);
		Collection<Object> collection = (Collection<Object>) Instance.create(rawType);

		for (String item : string.split("\\s*(?:,|;|\\R)\\s*"))
			if (!item.isBlank())
				collection.add(Converter.fromString(elementType, item));

		return collection;
	}

	private Type getElementType(Type type, Class<?> rawType)
	{
		if (rawType.isAnnotationPresent(gate.annotation.ElementType.class))
			return rawType.getAnnotation(gate.annotation.ElementType.class).value();
		if (type instanceof ParameterizedType parameterizedType)
			return parameterizedType.getActualTypeArguments()[0];
		return String.class;
	}
}