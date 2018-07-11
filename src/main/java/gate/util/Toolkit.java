package gate.util;

import gate.annotation.Color;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;

public class Toolkit
{

	public static boolean isEmpty(String string)
	{
		return string == null || string.isEmpty();
	}

	public static Object coalesce(Object obj1, Object obj2)
	{
		return obj1 != null ? obj1 : obj2;
	}

	public static Object coalesce(Object obj, Supplier<Object> supplier)
	{
		return obj != null ? obj : supplier.get();
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

	public static <T extends Comparable<T>> int compare(T obj1, T obj2)
	{
		if (obj1 != null)
			if (obj2 != null)
				return obj1.compareTo(obj2);
			else
				return 1;
		else if (obj2 != null)
			return -1;
		else
			return 0;

	}

	public static String write(Object object) throws ConversionException
	{
		return Converter.toString(object);
	}

	public static String print(Object object) throws ConversionException
	{
		return Converter.toText(object);
	}

	public static String print(Object object, String format) throws ConversionException
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
			return Arrays.asList(obj);
		else
			return Collections.singletonList(obj);
	}

	public static String html(Object object)
	{
		return object != null ? object.toString().replaceAll("\\n", "<br/>") : "";
	}

	public static String qs(HttpServletRequest request, String qs)
	{
		if (request.getQueryString() == null)
			return qs;
		if (request.getQueryString().isEmpty())
			return qs;
		if (!"GET".equals(request.getMethod()))
			return qs;
		if (request.getParameter("$keep") == null)
			return qs;
		return request.getQueryString();
	}

	public static String color(Object obj)
	{
		try
		{
			if (obj == null)
				return "#000000";
			AnnotatedElement type = obj instanceof Enum<?> ? obj.getClass().getField(((Enum<?>) obj).name()) : obj
					.getClass();
			return type.isAnnotationPresent(Color.class) ? type.getAnnotation(Color.class).value() : "#000000";
		} catch (NoSuchFieldException | SecurityException ex)
		{
			throw new AppError(ex);
		}
	}
}
