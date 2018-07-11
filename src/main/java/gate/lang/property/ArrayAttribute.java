package gate.lang.property;

import java.lang.reflect.Type;
import java.util.Objects;

class ArrayAttribute implements Attribute
{

	private final int index;
	private final Type type;

	ArrayAttribute(Type type, int index)
	{
		this.type = type;
		this.index = index;
	}

	@Override
	public Type getElementType()
	{
		return getRawType().getComponentType();
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public Class<?> getRawType()
	{
		return (Class<?>) type;
	}

	@Override
	public Object getValue(Object object)
	{
		if (object == null)
			return null;
		return ((Object[]) object)[index];
	}

	@Override
	public Object forceValue(Object object)
	{
		if (object == null)
			return null;
		Object[] array = ((Object[]) object);
		if (array.length > index
				&& array[index] == null)
			array[index] = createInstance(getElementRawType());
		return array[index];
	}

	@Override
	public void setValue(Object object, Object value)
	{
		((Object[]) object)[index] = value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ArrayAttribute
				&& type.equals(((ArrayAttribute) obj).type)
				&& index == ((ArrayAttribute) obj).index;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.type) + index;
	}

	@Override
	public String toString()
	{
		return new StringBuilder("[")
				.append(index).append("]")
				.toString();
	}
}
