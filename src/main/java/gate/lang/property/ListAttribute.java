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
		return object != null
			&& ((List<?>) object).size() > index
			? ((List<?>) object).get(index) : null;
	}

	@Override
	public Object forceValue(Object object)
	{
		List list = (List) object;
		if (list.size() <= index)
		{
			Class clazz = getRawType();
			while (list.size() <= index)
				list.add(createInstance(clazz));
		}
		return ((List<?>) object).get(index);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		((List) object).set(index, value);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ListAttribute
			&& index == ((ListAttribute) obj).index
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
