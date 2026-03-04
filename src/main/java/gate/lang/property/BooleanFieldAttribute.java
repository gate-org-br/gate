package gate.lang.property;

import gate.function.ObjBooleanConsumer;
import gate.function.ObjBooleanFunction;
import gate.function.ToBooleanFunction;
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

class BooleanFieldAttribute extends AbstractFieldAttribute
{

	private final ToBooleanFunction<Object> getter;
	private final ObjBooleanConsumer<Object> setter;
	private final ObjBooleanFunction<Object, Object> fluentSetter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	BooleanFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != boolean.class)
			throw new IllegalArgumentException(
					"BooleanFieldAttribute only supports boolean fields: %s.%s"
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
		return getBoolean(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (!(value instanceof Boolean b))
			throw new IllegalArgumentException(
					"Attempt to write a non boolean value to a boolean attribute: " + value);
		setBoolean(object, b);
	}

	@Override
	public Object forceValue(Object object)
	{
		return getBoolean(object);
	}

	@Override
	public boolean getBoolean(Object object)
	{
		try
		{
			if (getter != null)
				return getter.applyAsBoolean(object);
			if (fieldGetter != null)
				return (boolean) fieldGetter.get(object);
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
	public void setBoolean(Object object, boolean value)
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
	private ToBooleanFunction<Object> createGetterLambda()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ToBooleanFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsBoolean",
					MethodType.methodType(ToBooleanFunction.class),
					MethodType.methodType(boolean.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private ObjBooleanConsumer<Object> createSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() != void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjBooleanConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjBooleanConsumer.class),
					MethodType.methodType(void.class, Object.class, boolean.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private ObjBooleanFunction<Object, Object> createFluentSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getReturnType() == void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjBooleanFunction<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(ObjBooleanFunction.class),
					MethodType.methodType(Object.class, Object.class, boolean.class),
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
		return obj instanceof BooleanFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
