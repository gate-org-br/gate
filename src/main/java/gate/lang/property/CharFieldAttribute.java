package gate.lang.property;

import gate.function.ObjCharConsumer;
import gate.function.ObjCharFunction;
import gate.function.ToCharFunction;
import gate.util.Reflection;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

class CharFieldAttribute extends AbstractFieldAttribute
{

	private final ToCharFunction<Object> getter;
	private final ObjCharConsumer<Object> setter;
	private final ObjCharFunction<Object, Object> fluentSetter;

	CharFieldAttribute(Field field)
	{
		super(field);
		if (field.getType() != char.class)
			throw new IllegalArgumentException(
					"CharFieldAttribute only supports char fields: %s.%s"
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
		return getChar(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		if (!(value instanceof Character c))
			throw new IllegalArgumentException(
					"Attempt to write a non char value to a char attribute: " + value);
		setChar(object, c);
	}

	@Override
	public Object forceValue(Object object)
	{
		return getChar(object);
	}

	@Override
	public char getChar(Object object)
	{
		try
		{
			return getter.applyAsChar(object);
		} catch (RuntimeException ex)
		{
			throw ex instanceof IllegalStateException
					? ex
					: new IllegalStateException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setChar(Object object, char value)
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
	private static ToCharFunction<Object> createGetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ToCharFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsChar",
					MethodType.methodType(ToCharFunction.class),
					MethodType.methodType(char.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjCharConsumer<Object> createSetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjCharConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjCharConsumer.class),
					MethodType.methodType(void.class, Object.class, char.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjCharFunction<Object, Object> createFluentSetterLambda(Method method)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (ObjCharFunction<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(ObjCharFunction.class),
					MethodType.methodType(Object.class, Object.class, char.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create fluent setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ToCharFunction<Object> createFieldGetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectGetter(field);

			return (ToCharFunction<Object>) LambdaMetafactory.metafactory(
					lookup,
					"applyAsChar",
					MethodType.methodType(ToCharFunction.class),
					MethodType.methodType(char.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create field getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjCharConsumer<Object> createFieldSetterLambda(Field field)
	{
		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflectSetter(field);

			return (ObjCharConsumer<Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(ObjCharConsumer.class),
					MethodType.methodType(void.class, Object.class, char.class),
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
		return obj instanceof CharFieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}
}
