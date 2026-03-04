package gate.lang.property;

import gate.function.ObjFloatConsumer;
import gate.function.ObjFloatFunction;
import gate.function.ToFloatFunction;
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

class FloatFieldAttribute extends AbstractFieldAttribute
{

	private final ToFloatFunction<Object> getter;
	private final ObjFloatConsumer<Object> setter;
	private final ObjFloatFunction<Object, Object> fluentSetter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	FloatFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != float.class)
			throw new IllegalArgumentException(
					"FloatFieldAttribute only supports float fields: %s.%s"
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
				return getter.applyAsFloat(object);
			if (fieldGetter != null)
				return (float) fieldGetter.get(object);
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
	public void setFloat(Object object, float value)
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

	@SuppressWarnings("unchecked")
	private ToFloatFunction<Object> createGetterLambda()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ToFloatFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsFloat",
					MethodType.methodType(ToFloatFunction.class),
					MethodType.methodType(float.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private ObjFloatConsumer<Object> createSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() != void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjFloatConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjFloatConsumer.class),
					MethodType.methodType(void.class, Object.class, float.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private ObjFloatFunction<Object, Object> createFluentSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() == void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjFloatFunction<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(ObjFloatFunction.class),
					MethodType.methodType(Object.class, Object.class, float.class),
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
		return obj instanceof FloatFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
