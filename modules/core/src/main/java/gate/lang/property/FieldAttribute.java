package gate.lang.property;

import gate.annotation.NullSafe;
import gate.util.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FieldAttribute extends AbstractFieldAttribute
{
	private static final ConcurrentHashMap<Field, FieldAttribute> CACHE = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Map<String, FieldAttribute>> ATTRIBUTES = new ConcurrentHashMap<>();

	private final MethodHandle getter;
	private final MethodHandle setter;
	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;

	public static FieldAttribute of(Field field)
	{
		return CACHE.computeIfAbsent(field, FieldAttribute::new);
	}

	private FieldAttribute(Field field)
	{
		super(field);

		getter = createGetter();
		fieldGetter = createFieldGetter();
		setter = createSetter();
		fieldSetter = createFieldSetter();
	}

	@Override
	public Object getValue(Object object)
	{
		try
		{
			if (getter != null)
				return getter.invoke(object);

			if (fieldGetter != null)
				return fieldGetter.get(object);

			throw new UnsupportedOperationException(
					"The property %s of class %s does not support reading"
							.formatted(field.getName(), field.getDeclaringClass().getName()));
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException)
				throw (RuntimeException) ex;
			throw new IllegalStateException("Failed to access field attribute", ex);
		}
	}

	public Object getFieldValue(Object object)
	{
		try
		{
			if (fieldGetter != null)
				return fieldGetter.get(object);

			throw new UnsupportedOperationException(
					"The property %s of class %s does not support direct field reading"
							.formatted(field.getName(), field.getDeclaringClass().getName()));
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException)
				throw (RuntimeException) ex;
			throw new IllegalStateException("Failed to access field attribute", ex);
		}
	}

	@Override
	public void setValue(Object object, Object value)
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
			if (ex instanceof RuntimeException)
				throw (RuntimeException) ex;
			throw new IllegalStateException("Failed to access field attribute", ex);
		}
	}

	@Override
	public Object forceValue(Object object)
	{
		Object value = getValue(object);
		if (value != null)
			return value;

		value = createInstance(getRawType());
		setValue(object, value);
		return value;
	}

	private MethodHandle createGetter()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null
					|| method.getReturnType().isPrimitive()
					|| method.isAnnotationPresent(NullSafe.class))
				return null;
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn
					(method.getDeclaringClass(), MethodHandles.lookup());
			return lookup.unreflect(method);
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException runtimeException)
				throw runtimeException;
			throw new IllegalStateException("Failed to access getter method", ex);
		}
	}

	private MethodHandle createSetter()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getParameters()[0].getType()
					.isPrimitive() || method.getReturnType() != void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(),
					MethodHandles.lookup());
			return lookup.unreflect(method);
		} catch (Throwable ex)
		{
			if (ex instanceof RuntimeException runtimeException)
				throw runtimeException;
			throw new IllegalStateException("Failed to access setter method", ex);
		}
	}

	private VarHandle createFieldGetter()
	{
		try
		{
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
			if (setter != null || Modifier.isFinal(field.getModifiers()))
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
		return obj instanceof FieldAttribute attribute
				&& Objects.equals(field, attribute.field);
	}

	public static Map<String, FieldAttribute> getAttributes(Class<?> type)
	{
		return ATTRIBUTES.computeIfAbsent(type, c ->
				Reflection.getFields(c).stream()
						.filter(e -> !Modifier.isStatic(e.getModifiers()))
						.filter(e -> !Modifier.isTransient(e.getModifiers()))
						.filter(e -> !e.getDeclaringClass().getModule().isNamed()
								|| e.getDeclaringClass().getModule().isOpen(e.getDeclaringClass().getPackageName()))
						.collect(Collectors.toMap(Field::getName, FieldAttribute::of)));
	}
}