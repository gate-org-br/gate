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

	MapAttribute(Type type, Object key)
	{
		this.key = key;
		this.type = type;
	}

	@Override
	public Type getElementType()
	{
		if (type instanceof Class<?>)
		{
			Class<?> clazz = (Class<?>) type;
			if (clazz.isAnnotationPresent(ElementType.class))
				return clazz.getAnnotation(ElementType.class).value();
			else if (clazz.isArray())
				return clazz.getComponentType();
		} else if (type instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> clazz = (Class<?>) parameterizedType.getRawType();

			if (clazz.isAnnotationPresent(ElementType.class))
				return clazz.getAnnotation(ElementType.class).value();
			else if (clazz.isArray())
				return clazz.getComponentType();
			else if (List.class.isAssignableFrom(clazz))
				return parameterizedType.getActualTypeArguments()[0];
			else if (Map.class.isAssignableFrom(clazz))
				return parameterizedType.getActualTypeArguments()[1];
		}
		return Object.class;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public Class<?> getRawType()
	{
		return type instanceof ParameterizedType
				? (Class<?>) ((ParameterizedType) type).getRawType() : (Class<?>) type;
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
