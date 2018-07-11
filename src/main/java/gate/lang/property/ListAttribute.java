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

	ListAttribute(Type type, int index)
	{
		this.type = type;
		this.index = index;
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
	public Class<?> getRawType()
	{
		return type instanceof ParameterizedType
				? (Class<?>) ((ParameterizedType) type).getRawType() : (Class<?>) type;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public Object getValue(Object object)
	{
		return object != null
				? ((List<?>) object).get(index) : null;
	}

	@Override
	public Object forceValue(Object object)
	{
		List list = (List) object;
		if (list.size() <= index)
		{
			Class type = getRawType();
			while (list.size() <= index)
				list.add(createInstance(type));
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
		return new StringBuilder("[")
				.append(index).append("]")
				.toString();
	}
}
