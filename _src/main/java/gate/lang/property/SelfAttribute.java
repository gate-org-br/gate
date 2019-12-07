package gate.lang.property;

import gate.error.PropertyError;
import java.lang.reflect.Type;
import java.util.Objects;

public class SelfAttribute implements JavaIdentifierAttribute
{

	private final Class<?> type;

	SelfAttribute(Class<?> type)
	{
		this.type = type;
	}

	@Override
	public Type getGenericType()
	{
		return type;
	}

	@Override
	public Class<?> getRawType()
	{
		return type;
	}

	@Override
	public Type getElementType()
	{
		return Object.class;
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
	public void setValue(Object object, Object value)
	{
		throw new PropertyError("Attempt to set value of self.");
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof SelfAttribute
				&& Objects.equals(type, ((SelfAttribute) obj).type);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	@Override
	public String toString()
	{
		return "this";
	}
}
