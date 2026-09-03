package gate.lang.property;

import gate.annotation.ElementType;
import gate.adapter.collector.Collector;
import gate.util.Toolkit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class CollectionAttribute implements Attribute
{

	private final Type type;
	private final Attribute attribute;
	private final Class<?> rawType;
	private final Type elementType;

	CollectionAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		this.type = attribute.getElementType();

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
		return attribute.getValue(object);
	}

	@Override
	public Object forceValue(Object object)
	{
		return attribute.forceValue(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (attribute instanceof SelfAttribute)
		{
			Collection<Object> target = (Collection<Object>) object;
			target.clear();
			target.addAll(Toolkit.collection(value));
			return;
		}
		attribute.setValue(object, collect(value));
	}

	public Collector getCollector()
	{
		return attribute.getCollector();
	}

	public Type getCollectionType()
	{
		return attribute.getGenericType();
	}

	@Override
	public boolean matches(Parameter parameter)
	{
		return attribute.matches(parameter);
	}

	private Object collect(Object value)
	{
		Collection<?> collection = Toolkit.collection(value);
		if (attribute.getRawType().isAssignableFrom(collection.getClass()))
			return collection;

		return getCollector().ofArray(attribute.getGenericType(), collection.toArray());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof CollectionAttribute other
				&& Objects.equals(attribute, other.attribute)
				&& Objects.equals(type, other.type);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(attribute, type);
	}

	@Override
	public String toString()
	{
		return attribute + "[]";
	}

}
