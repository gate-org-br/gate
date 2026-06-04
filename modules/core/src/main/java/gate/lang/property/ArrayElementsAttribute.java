package gate.lang.property;

import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Objects;

public class ArrayElementsAttribute implements Attribute
{
	private final Type type;

	ArrayElementsAttribute(Type type)
	{
		this.type = type;
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
		return null;
	}

	@Override
	public void setValue(Object object, Object value)
	{
		throw new UnsupportedOperationException("Array elements must be set while constructing the array");
	}

	@Override
	public Object forceValue(Object object)
	{
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ArrayElementsAttribute attribute
		       && Objects.equals(type, attribute.type);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(type);
	}

	@Override
	public String toString()
	{
		return "[]";
	}
}
