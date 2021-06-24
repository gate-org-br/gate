package gate.util;

import gate.converter.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
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
			return Collections.emptyList();
		if (obj instanceof Iterable<?>)
			return (Iterable) obj;
		if (obj instanceof Object[])
			return Arrays.asList((Object[]) obj);
		return Collections.singleton(obj);
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
			return Collections.emptyList();
		else if (obj instanceof Collection)
			return (Collection) obj;
		else if (obj instanceof Object[])
			return Arrays.asList((Object[]) obj);
		else
			return Collections.singleton(obj);
	}

	public static List<Object> list(Object obj)
	{
		if (obj == null)
			return Collections.emptyList();
		else if (obj instanceof List)
			return (List) obj;
		else if (obj instanceof Collection)
			return new ArrayList((Collection) obj);
		else if (obj.getClass().isArray())
			return Collections.singletonList(obj);
		else
			return Collections.singletonList(obj);
	}

	public static Object coalesce(Object a, Object b)
	{
		return a != null ? a : b;
	}

	public static Object coalesce(Object obj, Supplier<Object> supplier)
	{
		return obj != null ? obj : supplier.get();
	}

	public static void sleep(int value)
	{
		try
		{
			Thread.sleep(value);
		} catch (InterruptedException ex)
		{
			Logger.getLogger(Toolkit.class.getName())
				.log(Level.SEVERE, null, ex);
		}
	}
}
