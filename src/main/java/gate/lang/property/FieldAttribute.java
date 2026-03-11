package gate.lang.property;

import gate.annotation.NullSafe;
import gate.util.Reflection;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldAttribute extends AbstractFieldAttribute
{
	private static final ConcurrentHashMap<Field, FieldAttribute> CACHE = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Map<String, FieldAttribute>> ATTRIBUTES = new ConcurrentHashMap<>();

	private final VarHandle fieldGetter;
	private final VarHandle fieldSetter;
	private final Function<Object, Object> getter;
	private final BiConsumer<Object, Object> setter;
	private final BiFunction<Object, Object, Object> fluentSetter;

	public static FieldAttribute of(Field field)
	{
		return CACHE.computeIfAbsent(field, FieldAttribute::new);
	}

	private FieldAttribute(Field field)
	{
		super(field);

		getter = createGetterLambda();
		fieldGetter = createFieldGetter();
		setter = createSetterLambda();
		fluentSetter = createFluentSetterLambda();
		fieldSetter = createFieldSetter();
	}

	@Override
	public Object getValue(Object object)
	{
		try
		{
			if (getter != null)
				return getter.apply(object);

			if (fieldGetter != null)
				return fieldGetter.get(object);

			throw new UnsupportedOperationException(
					"The property %s of class %s does not support reading"
							.formatted(field.getName(), field.getDeclaringClass().getName()));
		} catch (RuntimeException ex)
		{
			throw ex instanceof IllegalStateException
					? ex
					: new IllegalStateException("Failed to access field attribute", ex);
		}
	}

	@Override
	public void setValue(Object object, Object value)
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
					: new IllegalStateException("Failed to access field attribute", ex);
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

	private Function<Object, Object> createGetterLambda()
	{
		try
		{
			Method method = Reflection.findGetter(field).orElse(null);
			if (method == null || method.getReturnType().isPrimitive()
				|| method.isAnnotationPresent(NullSafe.class))
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			//noinspection unchecked
			return (Function<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(Function.class),
					MethodType.methodType(Object.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create getter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private BiConsumer<Object, Object> createSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getParameters()[0].getType().isPrimitive() || method.getReturnType() != void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (BiConsumer<Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(BiConsumer.class),
					MethodType.methodType(void.class, Object.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create setter lambda", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private BiFunction<Object, Object, Object> createFluentSetterLambda()
	{
		try
		{
			Method method = Reflection.findSetter(field).orElse(null);
			if (method == null || method.getParameters()[0].getType().isPrimitive() || method.getReturnType() == void.class)
				return null;

			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);

			return (BiFunction<Object, Object, Object>) LambdaMetafactory.metafactory(
					lookup,
					"apply",
					MethodType.methodType(BiFunction.class),
					MethodType.methodType(Object.class, Object.class, Object.class),
					impl,
					impl.type()
			).getTarget().invokeExact();
		} catch (Throwable ex)
		{
			throw new IllegalStateException("Failed to create fluent setter lambda", ex);
		}
	}

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
		return obj instanceof FieldAttribute attribute
			   && Objects.equals(field, attribute.field);
	}

	public static Map<String, FieldAttribute> getAttributes(Class<?> type)
	{
		return ATTRIBUTES.computeIfAbsent(type, c ->
				Reflection.getFields(c).stream()
						.filter(e -> !Modifier.isStatic(e.getModifiers()))
						.filter(e -> !Modifier.isTransient(e.getModifiers()))
						.collect(Collectors.toMap(Field::getName, FieldAttribute::of)));
	}
}