package gate.lang.property;

import gate.util.Reflection;
import gate.util.Toolkit;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

public class ArrayElementsAttribute implements Attribute
{
	private final Type type;
	private final Attribute attribute;

	ArrayElementsAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		this.type = attribute.getElementType();
	}

	@Override
	public Type getGenericType()
	{
		return type;
	}

	@Override
	public Type getElementType()
	{
		return type;
	}

	@Override
	public Class<?> getRawType()
	{
		return Reflection.getRawType(type);
	}

	@Override
	public Object getValue(Object object)
	{
		return attribute.getValue(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		attribute.setValue(object, array(value));
	}

	private Object array(Object value)
	{
		Collection<?> collection = Toolkit.collection(value);
		Object array = Array.newInstance(getRawType(), collection.size());
		int index = 0;
		for (Object element : collection)
			Array.set(array, index++, element);
		return array;
	}

	@Override
	public Object forceValue(Object object)
	{
		return attribute.forceValue(object);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ArrayElementsAttribute other
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
