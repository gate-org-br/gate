package gate.lang.property;

import gate.function.ObjShortConsumer;
import gate.function.ObjShortFunction;
import gate.function.ToShortFunction;
import gate.util.Reflection;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

class ShortFieldAttribute extends AbstractFieldAttribute
{

	private final ToShortFunction<Object> getter;
	private final ObjShortConsumer<Object> setter;
	private final ObjShortFunction<Object, Object> fluentSetter;

	ShortFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != short.class)
			throw new IllegalArgumentException(
					"ShortFieldAttribute only supports short fields: %s.%s"
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
		return getShort(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (!(value instanceof Number number))
			throw new IllegalArgumentException(
					"Attempt to write a non numeric value to a short attribute: " + value);
		setShort(object, number.shortValue());
	}

	@Override
	public Object forceValue(Object object)
	{
		return getShort(object);
	}

	@Override
	public short getShort(Object object)
	{
		try
		{
			return getter.applyAsShort(object);
		} catch (RuntimeException ex)
		{
			throw ex instanceof IllegalStateException
					? ex
					: new IllegalStateException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setShort(Object object, short value)
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

	@SuppressWarnings("unchecked")
	private static ToShortFunction<Object> createGetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ToShortFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsShort",
					MethodType.methodType(ToShortFunction.class),
					MethodType.methodType(short.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjShortConsumer<Object> createSetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjShortConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjShortConsumer.class),
					MethodType.methodType(void.class, Object.class, short.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjShortFunction<Object, Object> createFluentSetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjShortFunction<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(ObjShortFunction.class),
					MethodType.methodType(Object.class, Object.class, short.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create fluent setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ToShortFunction<Object> createFieldGetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectGetter(field);

			return (ToShortFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsShort",
					MethodType.methodType(ToShortFunction.class),
					MethodType.methodType(short.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create field getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjShortConsumer<Object> createFieldSetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectSetter(field);

			return (ObjShortConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjShortConsumer.class),
					MethodType.methodType(void.class, Object.class, short.class),
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
		return obj instanceof ShortFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
