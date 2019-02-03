package gate.lang.property;

import gate.annotation.ElementType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class MapAttribute implements Attribute
{

	private final Type type;
	private final Object key;
	private final Class<?> rawType;
	private final Type elementType;

	MapAttribute(Type type, Object key)
	{
		this.key = key;
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
		} else if (type instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) type;
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
	public Type getType()
	{
		return type;
	}

	@Override
	public Class<?> getRawType()
	{
		return rawType;
	}

	@Override
	public Object getValue(Object object)
	{
		if (object == null)
			return null;
		Map map = (Map) object;
		return map.get(key);
	}

	@Override
	public Object forceValue(Object object)
	{
		if (object == null)
			return null;
		Map map = (Map) object;
		if (!map.containsKey(key))
			map.put(key, createInstance(getElementRawType()));
		return map.get(key);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		((Map) object).put(key, value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MapAttribute
			&& Objects.equals(key, ((MapAttribute) obj).key)
			&& Objects.equals(type, ((MapAttribute) obj).type);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	@Override
	public String toString()
	{
		return key instanceof String
			? new StringBuilder("['").append(key).append("']").toString()
			: new StringBuilder("[").append(key).append("]").toString();
	}
}
