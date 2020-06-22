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

	CollectionAttribute(Type type)
	{
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
	public Class<?> getRawType()
	{
		return type instanceof ParameterizedType
			? (Class<?>) ((ParameterizedType) type).getRawType() : (Class<?>) type;
	}

	@Override
	public Type getGenericType()
	{
		return type;
	}

	@Override
	public Object getValue(Object object)
	{
		return object;
	}

	@Override
	public Object forceValue(Object object)
	{
		return object;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object object, Object value)
	{
		((Collection) object).add(value);
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
