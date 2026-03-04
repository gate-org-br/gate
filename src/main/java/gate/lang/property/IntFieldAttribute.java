package gate.lang.property;

import gate.function.ObjIntFunction;
import gate.util.Reflection;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

class IntFieldAttribute extends AbstractFieldAttribute
{
	private final ToIntFunction<Object> getter;
	private final ObjIntConsumer<Object> setter;
	private final ObjIntFunction<Object, Object> fluentSetter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	IntFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != int.class)
			throw new IllegalArgumentException(
					"IntFieldAttribute only supports int fields: %s.%s"
							.formatted(field.getDeclaringClass().getName(), field.getName()));

		getter = createGetterLambda();
		fieldGetter = createFieldGetter();
		setter = createSetterLambda();
		fluentSetter = createFluentSetterLambda();
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
			throw new IllegalArgumentException(
					"Attempt to write a non numeric value to an int attribute: " + value);

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
				return getter.applyAsInt(object);
			if (fieldGetter != null)
				return (int) fieldGetter.get(object);
			throw new UnsupportedOperationException(
					"The property %s of class %s does not support reading"
							.formatted(field.getName(), field.getDeclaringClass().getName()));
		} catch (RuntimeException ex)
		{
			throw ex instanceof IllegalStateException
					? ex
					: new IllegalStateException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setInt(Object object, int value)
	{
		if (setter == null && fluentSetter == null && fieldSetter == null)
			throw new UnsupportedOperationException(
					"The property %s of class %s does not support writing"
							.formatted(field.getName(), field.getDeclaringClass().getName()));

		try
		{
			if (setter != null)
				setter.accept(object, value);
			else if (fluentSetter != null)
				fluentSetter.apply(object, value);
			else
				fieldSetter.set(object, value);
		} catch (RuntimeException ex)
		{
			throw ex instanceof IllegalStateException
					? ex
					: new IllegalStateException(ex.getMessage(), ex);
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

	@SuppressWarnings("unchecked")
	private ToIntFunction<Object> createGetterLambda()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ToIntFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsInt",
					MethodType.methodType(ToIntFunction.class),
					MethodType.methodType(int.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private ObjIntConsumer<Object> createSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() != void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjIntConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjIntConsumer.class),
					MethodType.methodType(void.class, Object.class, int.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private ObjIntFunction<Object, Object> createFluentSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() == void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjIntFunction<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(ObjIntFunction.class),
					MethodType.methodType(Object.class, Object.class, int.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create fluent setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private VarHandle createFieldGetter()
	{
		try
		{
			if (getter != null)
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
			if (setter != null || fluentSetter != null || Modifier.isFinal(field.getModifiers()))
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
		return obj instanceof IntFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
