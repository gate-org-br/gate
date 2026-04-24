package gate.lang.property;

import gate.annotation.ElementType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ListAttribute implements Attribute
{

	private final int index;
	private final Type type;
	private final Class<?> rawType;
	private final Type elementType;

	ListAttribute(Type type, int index)
	{
		this.type = type;
		this.index = index;

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
		return object != null && ((List<?>) object).size() > index ? ((List<?>) object).get(index)
				: null;
	}

	@Override
	public Object forceValue(Object object)
	{
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) object;
		if (list.size() <= index)
		{
			Class<?> clazz = getRawType();
			while (list.size() <= index)
				list.add(createInstance(clazz));
		}
		return list.get(index);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object object, Object value)
	{
		var list = ((List<Object>) object);

		if (list.size() < index + 1)
			while (list.size() < index + 1)
				list.add(null);
		((List<Object>) object).set(index, value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ListAttribute && index == ((ListAttribute) obj).index
				&& Objects.equals(type, ((ListAttribute) obj).type);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	@Override
	public String toString()
	{
		return "[" + index + "]";
	}
}
