package gate.util;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Toolkit
{

	public static boolean isEmpty(String string)
	{
		return string == null || string.isBlank();
	}

	public static boolean notEmpty(String string)
	{
		return string != null && !string.isBlank();
	}

	public static boolean isEmpty(String... string)
	{
		return Stream.of(string).allMatch(Toolkit::isEmpty);
	}

	public static boolean notEmpty(String... string)
	{
		return Stream.of(string).allMatch(Toolkit::notEmpty);
	}

	public static boolean isEmpty(Object obj)
	{
		if (obj == null)
			return true;
		if (obj instanceof String string)
			return string.isEmpty();
		if (obj instanceof Map<?, ?> map)
			return map.isEmpty();
		if (obj instanceof Object[] objects)
			return objects.length == 0;
		if (obj instanceof Collection<?> collection)
			return collection.isEmpty();
		return false;
	}

	public static int getSize(Object obj)
	{
		if (obj == null)
			return 0;
		if (obj instanceof Collection<?> collection)
			return collection.size();
		if (obj instanceof Map<?, ?> map)
			return map.size();
		if (obj instanceof Object[] objects)
			return objects.length;
		if (obj instanceof String)
			return ((CharSequence) obj).length();
		return 1;
	}

	public static Iterable<?> iterable(Object obj)
	{
		if (obj == null)
			return List.of();
		if (obj instanceof Iterable<?> iterable)
			return iterable;
		if (obj instanceof Object[] objects)
			return Arrays.asList(objects);
		if (obj instanceof Map<?, ?> map)
			return map.entrySet();
		return List.of(obj);
	}

	public static Stream<?> stream(Object obj)
	{
		if (obj == null)
			return Stream.empty();
		else if (obj instanceof Collection)
			return ((Collection<?>) obj).stream();
		else if (obj instanceof Object[] objects)
			return Stream.of(objects);
		else if (obj instanceof Map<?, ?> map)
			return map.entrySet().stream();
		else
			return Stream.of(obj);
	}

	public static Collection<?> collection(Object obj)
	{
		if (obj == null)
			return List.of();
		else if (obj instanceof Collection)
			return (Collection<?>) obj;
		else if (obj instanceof Object[] objects)
			return Arrays.asList(objects);
		else if (obj instanceof Map<?, ?> map)
			return map.entrySet();
		else
			return List.of(obj);
	}

	public static List<?> list(Object obj)
	{
		if (obj == null)
			return List.of();
		else if (obj instanceof List)
			return (List<?>) obj;
		else if (obj instanceof Collection)
			return new ArrayList<>((Collection<?>) obj);
		else if (obj instanceof Object[] objects)
			return Arrays.asList(objects);
		else if (obj instanceof Map<?, ?> map)
			return new ArrayList<>(map.entrySet());
		else
			return List.of(obj);
	}

	/**
	 * Checks if the object can be considered "false".
	 *
	 * @param obj the object to be checked.
	 * @return true if the object is null, a blank string, zero, an empty collection or an empty map
	 */
	public static boolean isFalsy(Object obj)
	{
		if (obj == null)
			return true;
		if (obj instanceof String string)
			return string.isBlank();
		if (obj instanceof Number number)
			return number.doubleValue() == 0.0;
		if (obj instanceof Collection<?> collection)
			return collection.isEmpty();
		if (obj instanceof Map<?, ?> map)
			return map.isEmpty();
		if (obj instanceof Object[] array)
			return array.length == 0;
		if (obj instanceof Boolean bool)
			return !bool;
		return false;
	}

	public static <T> T coalesce(T a, T b)
	{
		return a != null ? a : b;
	}

	public static <T> T coalesce(T obj, Supplier<T> supplier)
	{
		return obj != null ? obj : supplier.get();
	}

	@SafeVarargs
	public static <T> T coalesce(T... elements)
	{
		return Stream.of(elements).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public static boolean sleep(int value)
	{
		try
		{
			Thread.sleep(value);
			return true;
		} catch (InterruptedException ex)
		{
			return false;
		}
	}

	public static String escapeHTML(String string)
	{
		if (string == null)
			return null;
		StringBuilder result = new StringBuilder(Math.max(16, string.length()));
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&')
				result.append("&#").append((int) c).append(';');
			else
				result.append(c);
		}
		return result.toString();
	}

	public static List<String> parsePath(String path)
	{
		int index = 0;
		List<String> result = new ArrayList<>();

		while (index < path.length() && path.charAt(index) == '/')
		{
			index++;
			StringBuilder builder = new StringBuilder();
			for (; index < path.length() && path.charAt(index) != '/'; index++)
				builder.append(path.charAt(index));
			var string = builder.toString().trim();
			result.add(!string.isEmpty() && !"*".equals(string) ? string : null);
		}

		return result;
	}

	public static String format(Throwable exception)
	{
		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<ul class='TreeView'>");
		for (Throwable error = exception; error != null; error = error.getCause())
		{
			string.add("<li>");
			string.add(Toolkit.escapeHTML(error.getMessage()));
			string.add("<ul>");
			Stream.of(error.getStackTrace()).map(StackTraceElement::toString)
					.map(Toolkit::escapeHTML).forEach(e -> string.add("<li>").add(e).add("</li>"));
			string.add("</ul>");
			string.add("</li>");
		}
		string.add("</ul>");
		return string.toString();
	}
}
