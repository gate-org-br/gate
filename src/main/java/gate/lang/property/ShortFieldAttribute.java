package gate.lang.property;

import gate.function.ObjShortConsumer;
import gate.function.ObjShortFunction;
import gate.function.ToShortFunction;
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

class ShortFieldAttribute extends AbstractFieldAttribute
{

	private final ToShortFunction<Object> getter;
	private final ObjShortConsumer<Object> setter;
	private final ObjShortFunction<Object, Object> fluentSetter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	ShortFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != short.class)
			throw new IllegalArgumentException(
					"ShortFieldAttribute only supports short fields: %s.%s"
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
			if (getter != null)
				return getter.applyAsShort(object);
			if (fieldGetter != null)
				return (short) fieldGetter.get(object);
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
	public void setShort(Object object, short value)
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
	private ToShortFunction<Object> createGetterLambda()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null)
				return null;

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
	private ObjShortConsumer<Object> createSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() != void.class)
				return null;

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
	private ObjShortFunction<Object, Object> createFluentSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() == void.class)
				return null;

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
		return obj instanceof ShortFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
