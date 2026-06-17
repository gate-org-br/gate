package gate.lang.property;

import gate.util.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

class IntFieldAttribute extends AbstractFieldAttribute
{
	private final MethodHandle getter;
	private final MethodHandle setter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	IntFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != int.class)
			throw new IllegalArgumentException(
					"IntFieldAttribute only supports int fields: %s.%s"
							.formatted(field.getDeclaringClass().getName(), field.getName()));

		getter = createGetter();
		fieldGetter = createFieldGetter();
		setter = createSetter();
		fieldSetter = createFieldSetter();
	}

	@Override
	public Object getValue(Object object)
	{
		return getInt(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (!(value instanceof Number number))
			throw new IllegalArgumentException("Attempt to write a non numeric value to an int attribute: " + value);

		setInt(object, number.intValue());
	}

	@Override
	public Object forceValue(Object object)
	{
		return getInt(object);
	}

	@Override
	public byte getByte(Object object)
	{
		return (byte) getInt(object);
	}

	@Override
	public void setByte(Object object, byte value)
	{
		setInt(object, value);
	}

	@Override
	public short getShort(Object object)
	{
		return (short) getInt(object);
	}

	@Override
	public void setShort(Object object, short value)
	{
		setInt(object, value);
	}

	@Override
	public int getInt(Object object)
	{
		try
		{
			if (getter != null)
				return (int) getter.invokeExact(object);
			if (fieldGetter != null)
				return (int) fieldGetter.get(object);
			throw new UnsupportedOperationException(
					"The property %s of class %s does not support reading"
							.formatted(field.getName(), field.getDeclaringClass().getName()));
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException runtimeException)
				throw runtimeException;
			throw new IllegalStateException("Failed to access getter method", ex);
		}
	}

	@Override
	public void setInt(Object object, int value)
	{
		if (setter == null && fieldSetter == null)
			throw new UnsupportedOperationException(
					"The property %s of class %s does not support writing"
							.formatted(field.getName(), field.getDeclaringClass().getName()));

		try
		{
			if (setter != null)
				setter.invoke(object, value);
			else
				fieldSetter.set(object, value);
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException runtimeException)
				throw runtimeException;
			throw new IllegalStateException("Error trying to call %s setter method"
					.formatted(field.getName()), ex);
		}
	}

	@Override
	public long getLong(Object object)
	{
		return getInt(object);
	}

	@Override
	public void setLong(Object object, long value)
	{
		setInt(object, (int) value);
	}

	@Override
	public float getFloat(Object object)
	{
		return getInt(object);
	}

	@Override
	public void setFloat(Object object, float value)
	{
		setInt(object, (int) value);
	}

	@Override
	public double getDouble(Object object)
	{
		return getInt(object);
	}

	@Override
	public void setDouble(Object object, double value)
	{
		setInt(object, (int) value);
	}

	private MethodHandle createGetter()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return lookup.unreflect(method)
					.asType(MethodType.methodType(int.class, Object.class));
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException runtimeException)
				throw runtimeException;
			throw new IllegalStateException("Failed to access %s.%s getter method".formatted(field.getType().getName(), field.getName()), ex);
		}
	}

	private MethodHandle createSetter()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return lookup.unreflect(method);
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException runtimeException)
				throw runtimeException;
			throw new IllegalStateException("Failed to access %s.%s setter method".formatted(field.getType().getName(), field.getName()), ex);
		}
	}

	private VarHandle createFieldGetter()
	{
		try
		{
			if (getter != null || !Modifier.isPublic(field.getModifiers()))
				return null;

			return Reflection.findVarHandle(field);
		} catch (RuntimeException ex)
		{
			throw new IllegalStateException("Failed to access %s.%s getter var handle".formatted(field.getType().getName(), field.getName()), ex);
		}
	}

	private VarHandle createFieldSetter()
	{
		try
		{
			if (setter != null
					|| Modifier.isFinal(field.getModifiers())
					|| !Modifier.isPublic(field.getModifiers()))
				return null;

			return Reflection.findVarHandle(field);
		} catch (RuntimeException ex)
		{
			throw new IllegalStateException("Failed to access %s.%s setter var handle".formatted(field.getType().getName(), field.getName()), ex);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IntFieldAttribute attribute
			   && Objects.equals(field, attribute.field);
	}
}
