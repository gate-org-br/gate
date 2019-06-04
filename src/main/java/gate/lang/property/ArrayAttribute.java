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
	public Type getGenericType()
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
	public boolean getBoolean(Object object)
	{
		return object != null && ((boolean[]) object)[index];
	}

	@Override
	public void setBoolean(Object object, boolean value)
	{
		((boolean[]) object)[index] = value;
	}

	@Override
	public char getChar(Object object)
	{
		return object != null ? ((char[]) object)[index] : Character.MIN_VALUE;
	}

	@Override
	public void setChar(Object object, char value)
	{
		((char[]) object)[index] = value;
	}

	@Override
	public byte getByte(Object object)
	{
		return object != null ? ((byte[]) object)[index] : 0;
	}

	@Override
	public void setByte(Object object, byte value)
	{
		((byte[]) object)[index] = value;
	}

	@Override
	public short getShort(Object object)
	{
		return object != null ? ((short[]) object)[index] : 0;
	}

	@Override
	public void setShort(Object object, short value)
	{
		((short[]) object)[index] = value;
	}

	@Override
	public int getInt(Object object)
	{
		return object != null ? ((int[]) object)[index] : 0;
	}

	@Override
	public void setInt(Object object, int value)
	{
		((int[]) object)[index] = value;
	}

	@Override
	public long getLong(Object object)
	{
		return object != null ? ((long[]) object)[index] : 0;
	}

	@Override
	public void setLong(Object object, long value)
	{
		((long[]) object)[index] = value;
	}

	@Override
	public float getFloat(Object object)
	{
		return object != null ? ((float[]) object)[index] : 0;
	}

	@Override
	public void setFloat(Object object, float value)
	{
		((float[]) object)[index] = value;
	}

	@Override
	public double getDouble(Object object)
	{
		return object != null ? ((double[]) object)[index] : 0;
	}

	@Override
	public void setDouble(Object object, double value)
	{
		((double[]) object)[index] = value;
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
