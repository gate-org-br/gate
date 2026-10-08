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

class FloatFieldAttribute extends AbstractFieldAttribute
{

	private final MethodHandle getter;
	private final MethodHandle setter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	FloatFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != float.class)
			throw new IllegalArgumentException(
					"FloatFieldAttribute only supports float fields: %s.%s"
							.formatted(field.getDeclaringClass().getName(), field.getName()));

		getter = createGetter();
		fieldGetter = createFieldGetter();
		setter = createSetter();
		fieldSetter = createFieldSetter();
	}

	@Override
	public Object getValue(Object object)
	{
		return getFloat(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (!(value instanceof Number number))
			throw new IllegalArgumentException(
					"Attempt to write a non numeric value to a float attribute: " + value);
		setFloat(object, number.floatValue());
	}

	@Override
	public Object forceValue(Object object)
	{
		return getFloat(object);
	}

	@Override
	public float getFloat(Object object)
	{
		try
		{
			if (getter != null)
				return (float) getter.invokeExact(object);
			if (fieldGetter != null)
				return (float) fieldGetter.get(object);
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
	public void setFloat(Object object, float value)
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

	private MethodHandle createGetter()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return lookup.unreflect(method)
					.asType(MethodType.methodType(float.class, Object.class));
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

	@SuppressWarnings("unchecked")
	private VarHandle createFieldGetter()
	{
		try
		{
			if (getter != null || !Modifier.isPublic(field.getModifiers()))
				return null;

			return Reflection.findVarHandle(field);
		} catch (RuntimeException ex)
		{
			throw new IllegalStateException("Failed to create field getter handle", ex);
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
			throw new IllegalStateException("Failed to create field setter handle", ex);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FloatFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
