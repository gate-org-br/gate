package gate.lang.property;

import gate.annotation.ElementType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class MapAttribute implements Attribute
{

	private final Type genericType;
	private final Object key;
	private final Class<?> rawType;
	private final Type elementType;

	MapAttribute(Type genericType, Object key)
	{
		this.key = key;
		this.genericType = genericType;

		if (genericType instanceof Class<?>)
		{
			rawType = (Class<?>) genericType;
			if (rawType.isAnnotationPresent(ElementType.class))
				elementType = rawType.getAnnotation(ElementType.class).value();
			else if (rawType.isArray())
				elementType = rawType.getComponentType();
			else
				elementType = Object.class;
		} else if (genericType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
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
	public Type getGenericType()
	{
		return genericType;
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
			&& Objects.equals(genericType, ((MapAttribute) obj).genericType);
	}

	@Override
	public int hashCode()
	{
		return genericType.hashCode();
	}

	@Override
	public String toString()
	{
		return key instanceof String
			? new StringBuilder("['").append(key).append("']").toString()
			: new StringBuilder("[").append(key).append("]").toString();
	}
}
