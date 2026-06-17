package gate.util;

import gate.annotation.ElementType;

import java.beans.Introspector;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflection
{

	private static final Pattern PATTERN = Pattern.compile(
			"^([a-zA-Z_$][a-zA-Z0-9_$]*([.][a-zA-Z_$][a-zA-Z0-9_$]*)+([$][a-zA-Z_$][a-zA-Z0-9_$]*)*)(:(([a-zA-Z_$][a-zA-Z0-9_$]*)([(][)])?))?$");

	public static Type parameterizedType(Class<?> rawType, Type... typeArguments)
	{
		return new ParameterizedType()
		{
			@Override
			public Type[] getActualTypeArguments() {return typeArguments;}

			@Override
			public Type getRawType() {return rawType;}

			@Override
			public Type getOwnerType() {return null;}
		};
	}

	public static Class<?> getRawType(Type type)
	{
		if (type instanceof Class<?>)
			return (Class<?>) type;
		if (type instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) type).getRawType();
		else if (type instanceof GenericArrayType)
			return Array.newInstance((Class<?>) ((ParameterizedType) ((GenericArrayType) type)
					.getGenericComponentType()).getRawType(), 0).getClass();
		else
			return null;
	}

	public static Type getElementGenericType(Type type)
	{
		do
		{
			Class<?> clazz = getRawType(type);
			if (clazz.isArray())
				return clazz.getComponentType();
			if (Collection.class.isAssignableFrom(clazz))
			{
				if (clazz.isAnnotationPresent(ElementType.class))
					return clazz.getAnnotation(ElementType.class).value();
				if (type instanceof ParameterizedType parameterizedType)
					return parameterizedType.getActualTypeArguments()[0];
			}
			type = clazz.getGenericSuperclass();
		} while (type != null);

		return null;
	}

	public static Class<?> getKeyType(Type type)
	{
		Class<?> clazz = getRawType(type);
		if (Map.class.isAssignableFrom(clazz))
			return getRawType(((ParameterizedType) type).getActualTypeArguments()[0]);
		return null;
	}

	public static Type getValueGenericType(Type type)
	{
		Class<?> clazz = getRawType(type);
		if (Map.class.isAssignableFrom(clazz))
			return ((ParameterizedType) type).getActualTypeArguments()[1];
		return null;
	}

	public static List<Field> getFields(Class<?> clazz)
	{
		List<Field> fields = Stream.of(clazz.getDeclaredFields()).collect(Collectors.toList());
		if (clazz.getSuperclass() != null)
			fields.addAll(getFields(clazz.getSuperclass()));
		return fields;
	}


	/**
	 * Finds the specified field on the specified type, and it's super types.
	 *
	 * @param type type where to find the specified field
	 * @param name name of the field to be found
	 * @return an Optional describing the requested field of an empty Optional if the field does not exist
	 */
	public static Optional<Field> findField(Class<?> type, String name)
	{
		Optional<Field> field
				= Stream.of(type.getDeclaredFields()).filter(e -> e.getName().equals(name)).findAny();

		Class<?> supertype = type.getSuperclass();
		if (field.isEmpty() && supertype != null)
			return findField(supertype, name);

		field.ifPresent(e -> e.setAccessible(true));

		return field;
	}

	public static boolean isNull(Field field, Object object)
	{
		try
		{
			var accessible = field.canAccess(object);
			if (!accessible)
				field.setAccessible(true);
			try
			{
				return field.get(object) == null;
			} finally
			{
				if (!accessible)
					field.setAccessible(false);
			}
		} catch (IllegalAccessException ex)
		{
			throw new IllegalStateException(
					"Failed to check if field '%s' on '%s' is null"
							.formatted(field.getName(), field.getDeclaringClass().getName()), ex);
		}
	}

	/**
	 * Finds the specified method on the specified type, and it's super types.
	 *
	 * @param type           type where to find the specified method
	 * @param name           name of the method to be found
	 * @param parameterTypes types of the parameters of the method to be found
	 * @return an Optional describing the requested method of an empty Optional if the method does not exist
	 */
	public static Optional<Method> findMethod(Class<?> type, String name,
	                                          Class<?>... parameterTypes)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(name);
		Objects.requireNonNull(parameterTypes);

		return Stream.of(type.getMethods())
				.filter(e -> e.getName().equals(name))
				.filter(e -> Arrays.equals(e.getParameterTypes(), parameterTypes))
				.findFirst();
	}

	/**
	 * Finds the getter method on the specified property.
	 *
	 * @param type     type where to find the specified method
	 * @param property name of the property to be found
	 * @return an Optional describing the requested method of an empty Optional if the method does not exist
	 */
	@SuppressWarnings("unused")
	public static Optional<Method> findGetterByName(Class<?> type, String property)
	{
		var getter = type.isRecord() ? property :
				"get" + Character.toUpperCase(property.charAt(0))
				+ property.substring(1);
		return findMethodByName(type, getter);
	}

	/**
	 * Finds the specified method on the specified type, and it's super types.
	 *
	 * @param type type where to find the specified method
	 * @param name name of the method to be found
	 * @return an Optional describing the requested method of an empty Optional if the method does not exist
	 */
	public static Optional<Method> findMethodByName(Class<?> type, String name)
	{
		List<Method> methods
				= Stream.of(type.getMethods()).filter(e -> e.getName().equals(name))
				.collect(Collectors.toCollection(ArrayList::new));

		switch (methods.size())
		{
			case 0:
				return Optional.empty();
			case 1:
				return Optional.of(methods.get(0));
			default:
				return Optional.empty();
		}
	}

	/**
	 * Finds the field associated with the specified method.
	 *
	 * @param method method whose associated field is to be found
	 * @return an {@code Optional} describing the associated field, or an empty {@code Optional}
	 * if no matching field exists
	 */
	public static Optional<Field> findField(Method method)
	{
		var name = method.getName();
		if (name.startsWith("get") && name.length() > 3)
			return findField(method.getDeclaringClass(), Introspector.decapitalize(name.substring(3)));

		if (name.startsWith("set") && name.length() > 3)
			return findField(method.getDeclaringClass(), Introspector.decapitalize(name.substring(3)));

		if (name.startsWith("is") && name.length() > 2)
			return findField(method.getDeclaringClass(), Introspector.decapitalize(name.substring(2)));

		return findField(method.getDeclaringClass(), name);
	}


	/**
	 * Finds the getter method of the specified field.
	 *
	 * @param field the field associated with the requested getter
	 * @return an Optional describing the getter method of the specified field or an empty Optional if the field does not have a getter method
	 */
	public static Optional<Method> findGetter(Field field)
	{
		String name = field.getName();

		if (field.getDeclaringClass().isRecord())
			return findMethod(field.getDeclaringClass(), name);

		var get = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		var getter = findMethod(field.getDeclaringClass(), get);
		if (getter.isPresent())
			return getter;

		if (field.getType().equals(boolean.class)
				|| field.getType().equals(Boolean.class))
		{
			var is = "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			getter = findMethod(field.getDeclaringClass(), is);
			if (getter.isPresent())
				return getter;
		}

		return findMethod(field.getDeclaringClass(), name);
	}

	public static VarHandle findVarHandle(Field field)
	{
		try
		{
			if (!Modifier.isPublic(field.getModifiers()))
				throw new IllegalArgumentException("Field %s.%s is not public"
						.formatted(field.getDeclaringClass().getName(), field.getName()));
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return lookup.unreflectVarHandle(field);
		} catch (IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unused")
	public static Optional<MethodHandle> findGetterHandler(Field field)
	{
		return findGetter(field)
				.map(m ->
				{
					try
					{
						MethodHandles.Lookup lookup = MethodHandles.publicLookup();

						MethodHandle handle = lookup.unreflect(m);

						Class<?> returnType = m.getReturnType();

						return handle.asType(MethodType.methodType(returnType, Object.class));
					} catch (IllegalAccessException e)
					{
						throw new IllegalStateException(e);
					}
				});
	}

	@SuppressWarnings("unchecked")
	public static Optional<Function<Object, Object>> getter(Field field)
	{
		return findGetter(field)
				.map(method ->
				{
					try
					{
						MethodHandles.Lookup lookup = MethodHandles.publicLookup();
						MethodHandle impl = lookup.unreflect(method);
						MethodType sam = MethodType.methodType(Object.class, Object.class);
						MethodType instantiated = impl.type();
						return (Function<Object, Object>) LambdaMetafactory.metafactory(
								lookup,
								"apply",
								MethodType.methodType(Function.class),
								sam,
								impl,
								instantiated
						).getTarget().invokeExact();
					} catch (Throwable e)
					{
						throw new IllegalArgumentException("Failed to create field getter lambda for " + field, e);
					}
				});
	}


	@SuppressWarnings("unused")
	public static Optional<MethodHandle> findSetterHandler(Field field)
	{
		return findSetter(field)
				.map(m ->
				{
					try
					{
						MethodHandles.Lookup lookup = MethodHandles.publicLookup();

						MethodHandle handle = lookup.unreflect(m);

						Class<?> paramType = m.getParameterTypes()[0];

						return handle.asType(MethodType.methodType(void.class, Object.class, paramType));
					} catch (IllegalAccessException e)
					{
						throw new IllegalStateException(e);
					}
				});
	}

	/**
	 * Finds the setter method of the specified field.
	 *
	 * @param field the field associated with the requested setter
	 * @return an Optional describing the setter method of the specified field or an empty Optional if the field does not have a setter method
	 */
	public static Optional<Method> findSetter(Field field)
	{
		if (field.getDeclaringClass().isRecord())
			return Optional.empty();

		var name = field.getName();
		var type = field.getType();
		var set = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		var setter = findMethod(field.getDeclaringClass(), set, type);
		if (setter.isPresent())
			return setter;

		return findMethod(field.getDeclaringClass(), name, type);
	}

	public static Optional<? extends AnnotatedElement> find(String string)
			throws ClassNotFoundException
	{
		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches())
			return Optional.empty();

		Class<?> type = Thread.currentThread().getContextClassLoader().loadClass(matcher.group(1));

		String member = matcher.group(6);

		if (member == null)
			return Optional.of(type);

		return matcher.group(7) != null ? Reflection.findMethod(type, member)
				: Reflection.findField(type, member);
	}
}
