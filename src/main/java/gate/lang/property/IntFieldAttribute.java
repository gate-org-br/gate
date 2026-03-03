package gate.lang.property;

import gate.function.ObjIntFunction;
import gate.util.Reflection;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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

	IntFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != int.class)
			throw new IllegalArgumentException(
					"IntFieldAttribute only supports int fields: %s.%s"
							.formatted(field.getDeclaringClass().getName(), field.getName()));

		Method getterMethod = Reflection.findGetter(field).orElse(null);
		getter = getterMethod != null ? createGetterLambda(getterMethod) : createFieldGetterLambda(field);

		Method setterMethod = Reflection.findSetter(field).orElse(null);
		if (setterMethod != null)
			if (setterMethod.getReturnType() == void.class)
			{
				setter = createSetterLambda(setterMethod);
				fluentSetter = null;
			} else
			{
				setter = null;
				fluentSetter = createFluentSetterLambda(setterMethod);
			}
		else if (Modifier.isFinal(field.getModifiers()))
		{
			setter = null;
			fluentSetter = null;
		} else
		{
			setter = createFieldSetterLambda(field);
			fluentSetter = null;
		}
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
			return getter.applyAsInt(object);
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
		if (setter == null && fluentSetter == null)
			throw new UnsupportedOperationException(
					"The property %s of class %s does not support writing"
							.formatted(field.getName(), field.getDeclaringClass().getName()));

		try
		{
			if (setter != null)
				setter.accept(object, value);
			else
				fluentSetter.apply(object, value);
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
	private static ToIntFunction<Object> createGetterLambda(Method method)
	{
		try
		{
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
	private static ObjIntConsumer<Object> createSetterLambda(Method method)
	{
		try
		{
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
	private static ObjIntFunction<Object, Object> createFluentSetterLambda(Method method)
	{
		try
		{
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
	private static ToIntFunction<Object> createFieldGetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectGetter(field);

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
			throw new IllegalStateException("Failed to create field getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjIntConsumer<Object> createFieldSetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectSetter(field);

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
			throw new IllegalStateException("Failed to create field setter lambda", ex);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IntFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
