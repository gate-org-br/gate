package gate.lang.property;

import gate.annotation.ElementType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CollectionAttribute implements Attribute
{

	private final Type type;
	private final Class<?> rawType;
	private final Type elementType;

	CollectionAttribute(Type type)
	{
		this.type = type;

		if (type instanceof Class<?>)
		{
			rawType = (Class<?>) type;
			if (rawType.isAnnotationPresent(ElementType.class))
				elementType = rawType.getAnnotation(ElementType.class).value();
			else if (rawType.isArray())
				elementType = rawType.getComponentType();
			else
				elementType = Object.class;
		} else if (type instanceof ParameterizedType parameterizedType)
		{
			rawType = (Class<?>) parameterizedType.getRawType();

			if (rawType.isAnnotationPresent(ElementType.class))
				elementType = rawType.getAnnotation(ElementType.class).value();
			else if (rawType.isArray())
				elementType = rawType.getComponentType();
			else if (List.class.isAssignableFrom(rawType))
				elementType = parameterizedType.getActualTypeArguments()[0];
			else if (Map.class.isAssignableFrom(rawType))
				elementType = parameterizedType.getActualTypeArguments()[1];
			else
				elementType = Object.class;
		} else
		{
			rawType = Object.class;
			elementType = Object.class;
		}

	}

	@Override
	public Type getElementType()
	{
		return elementType;
	}

	@Override
	public Class<?> getRawType()
	{
		return rawType;
	}

	@Override
	public Type getGenericType()
	{
		return type;
	}

	@Override
	public Object getValue(Object object)
	{
		return null;
	}

	@Override
	public Object forceValue(Object object)
	{
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object object, Object value)
	{
		((Collection<Object>) object).add(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof CollectionAttribute
				&& Objects.equals(type, ((CollectionAttribute) obj).type);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	@Override
	public String toString()
	{
		return "[]";
	}

}
