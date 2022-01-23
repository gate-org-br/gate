package gate.util;

import gate.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflection
{

	private static final Pattern PATTERN
		= Pattern.compile("^([a-zA-Z_$][a-zA-Z0-9_$]*([.][a-zA-Z_$][a-zA-Z0-9_$]*)+([$][a-zA-Z_$][a-zA-Z0-9_$]*)*)(:(([a-zA-Z_$][a-zA-Z0-9_$]*)([(][)])?))?$");

	public static Class<?> getRawType(Type type)
	{
		if (type instanceof Class<?>)
			return (Class<?>) type;
		if (type instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) type).getRawType();
		else if (type instanceof GenericArrayType)
			return Array.newInstance(
				(Class<?>) ((ParameterizedType) ((GenericArrayType) type).getGenericComponentType()).getRawType(), 0)
				.getClass();
		else
			return null;
	}

	public static Type getElementType(Type type)
	{
		Class<?> clazz = getRawType(type);
		if (clazz.isAnnotationPresent(ElementType.class))
			return clazz.getAnnotation(ElementType.class).value();
		if (clazz.isArray())
			return clazz.getComponentType();
		if (Collection.class.isAssignableFrom(clazz))
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		return null;
	}

	public static List<Field> getFields(Class<?> clazz)
	{
		List<Field> fields
			= Stream.of(clazz.getDeclaredFields())
				.collect(Collectors.toList());
		if (clazz.getSuperclass() != null)
			fields.addAll(getFields(clazz.getSuperclass()));
		return fields;
	}

	/**
	 * Finds the specified field on the specified type and it's super types.
	 *
	 * @param type type where to find the specified field
	 * @param name name of the field to be found
	 *
	 * @return an Optional describing the requested field of an empty
	 * Optional if the field does not exists
	 */
	public static Optional<Field> findField(Class type, String name)
	{
		Optional<Field> field = Stream.of(type.getDeclaredFields())
			.filter(e -> e.getName().equals(name))
			.findAny();

		Class supertype = type.getSuperclass();
		if (!field.isPresent() && supertype != null)
			return findField(supertype, name);

		field.ifPresent(e -> e.setAccessible(true));

		return field;
	}

	/**
	 * Finds the specified method on the specified type and it's super
	 * types.
	 *
	 * @param type type where to find the specified method
	 * @param name name of the method to be found
	 * @param parameterTypes types of the parameters of the method to be
	 * found
	 *
	 * @return an Optional describing the requested method of an empty
	 * Optional if the method does not exists
	 */
	public static Optional<Method> findMethod(Class type, String name, Class... parameterTypes)
	{
		Optional<Method> method = Stream.of(type.getDeclaredMethods())
			.filter(e -> e.getName().equals(name))
			.filter(e -> Arrays.equals(e.getParameterTypes(), parameterTypes))
			.findAny();

		Class supertype = type.getSuperclass();
		if (!method.isPresent() && supertype != null)
			return findMethod(supertype, name, parameterTypes);

		method.ifPresent(e -> e.setAccessible(true));

		return method;
	}

	/**
	 * Finds the getter method of the specified field.
	 *
	 * @param field the field associated with the requested getter
	 *
	 * @return an Optional describing the getter method of the specified
	 * field or an empty Optional if the field does not have a getter method
	 */
	public static Optional<Method> findGetter(Field field)
	{
		String name = field.getName();
		Optional<Method> method = findMethod(field.getDeclaringClass(),
			"get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
		if (!method.isPresent()
			&& (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)))
		{
			name = field.getName();
			method = findMethod(field.getDeclaringClass(),
				"is" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
		}
		return method;
	}

	/**
	 * Finds the getter method of the specified field.
	 *
	 * @param field the field associated with the requested setter
	 *
	 * @return an Optional describing the setter method of the specified
	 * field or an empty Optional if the field does not have a setter method
	 */
	public static Optional<Method> findSetter(Field field)
	{
		StringBuilder name = new StringBuilder(field.getName());
		name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		name.insert(0, "set");
		return findMethod(field.getDeclaringClass(), name.toString(), field.getType());
	}

	public static Optional<? extends AnnotatedElement> find(String string)
		throws ClassNotFoundException
	{
		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches())
			return Optional.empty();

		Class type = Thread.currentThread().getContextClassLoader().loadClass(matcher.group(1));

		String member = matcher.group(6);

		if (member == null)
			return Optional.of(type);

		return matcher.group(7) != null
			? Reflection.findMethod(type, member)
			: Reflection.findField(type, member);
	}
}
