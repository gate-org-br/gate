package gate.util;

import gate.converter.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
		if (obj instanceof String)
			return ((String) obj).isEmpty();
		if (obj instanceof Map<?, ?>)
			return ((Map<?, ?>) obj).isEmpty();
		if (obj instanceof Object[])
			return ((Object[]) obj).length == 0;
		if (obj instanceof Collection<?>)
			return ((Collection<?>) obj).isEmpty();
		return false;
	}

	public static int getSize(Object obj)
	{
		if (obj == null)
			return 0;
		if (obj instanceof Collection<?>)
			return ((Collection<?>) obj).size();
		if (obj instanceof Map<?, ?>)
			return ((Map<?, ?>) obj).size();
		if (obj instanceof Object[])
			return ((Object[]) obj).length;
		if (obj instanceof String)
			return ((CharSequence) obj).length();
		return 1;
	}

	public static String write(Object object)
	{
		return Converter.toString(object);
	}

	public static String print(Object object)
	{
		return Converter.toText(object);
	}

	public static String print(Object object, String format)
	{
		return Converter.toText(object, format);
	}

	public static Iterable<Object> iterable(Object obj)
	{
		if (obj == null)
			return List.of();
		if (obj instanceof Iterable<?>)
			return (Iterable) obj;
		if (obj instanceof Object[])
			return Arrays.asList((Object[]) obj);
		return List.of(obj);
	}

	public static Stream<Object> stream(Object obj)
	{
		if (obj == null)
			return Stream.empty();
		else if (obj instanceof Collection)
			return ((Collection) obj).stream();
		else if (obj instanceof Object[])
			return Stream.of((Object[]) obj);
		else
			return Stream.of(obj);
	}

	public static Collection<Object> collection(Object obj)
	{
		if (obj == null)
			return List.of();
		else if (obj instanceof Collection)
			return (Collection) obj;
		else if (obj instanceof Object[])
			return Arrays.asList((Object[]) obj);
		else
			return List.of(obj);
	}

	public static List<? extends Object> list(Object obj)
	{
		if (obj == null)
			return List.of();
		else if (obj instanceof List)
			return (List) obj;
		else if (obj instanceof Collection)
			return new ArrayList((Collection) obj);
		else if (obj instanceof Object[])
			return Arrays.asList((Object[]) obj);
		else
			return List.of(obj);
	}

	public static <T> T coalesce(T a, T b)
	{
		return a != null ? a : b;
	}

	public static <T> T coalesce(T obj, Supplier<T> supplier)
	{
		return obj != null ? obj : supplier.get();
	}

	public static <T> T coalesce(T... elements)
	{
		return Stream.of(elements).filter(e -> e != null).findFirst().orElse(null);
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
}
