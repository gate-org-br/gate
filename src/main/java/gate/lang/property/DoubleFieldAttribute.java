package gate.lang.property;

import gate.function.ObjDoubleFunction;
import gate.util.Reflection;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

class DoubleFieldAttribute extends AbstractFieldAttribute
{

	private final ToDoubleFunction<Object> getter;
	private final ObjDoubleConsumer<Object> setter;
	private final ObjDoubleFunction<Object, Object> fluentSetter;

	DoubleFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != double.class)
			throw new IllegalArgumentException(
					"DoubleFieldAttribute only supports double fields: %s.%s"
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
		return getDouble(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (!(value instanceof Number number))
			throw new IllegalArgumentException(
					"Attempt to write a non numeric value to a double attribute: " + value);
		setDouble(object, number.doubleValue());
	}

	@Override
	public Object forceValue(Object object)
	{
		return getDouble(object);
	}

	@Override
	public double getDouble(Object object)
	{
		try
		{
			return getter.applyAsDouble(object);
		} catch (RuntimeException ex)
		{
			throw ex instanceof IllegalStateException
					? ex
					: new IllegalStateException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setDouble(Object object, double value)
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
	private static ToDoubleFunction<Object> createGetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ToDoubleFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsDouble",
					MethodType.methodType(ToDoubleFunction.class),
					MethodType.methodType(double.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjDoubleConsumer<Object> createSetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjDoubleConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjDoubleConsumer.class),
					MethodType.methodType(void.class, Object.class, double.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjDoubleFunction<Object, Object> createFluentSetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjDoubleFunction<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(ObjDoubleFunction.class),
					MethodType.methodType(Object.class, Object.class, double.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create fluent setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ToDoubleFunction<Object> createFieldGetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectGetter(field);

			return (ToDoubleFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsDouble",
					MethodType.methodType(ToDoubleFunction.class),
					MethodType.methodType(double.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create field getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjDoubleConsumer<Object> createFieldSetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectSetter(field);

			return (ObjDoubleConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjDoubleConsumer.class),
					MethodType.methodType(void.class, Object.class, double.class),
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
		return obj instanceof DoubleFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
