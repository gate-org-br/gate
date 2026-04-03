package gate;

import jakarta.enterprise.inject.spi.CDI;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public final class I18N
{

	private static final Map<String, Map<Locale, ResourceBundle>> CACHE = new ConcurrentHashMap<>();

	private I18N()
	{
	}

	public static Locale getLocale()
	{
		try
		{
			return CDI.current().select(GateContext.class).get().get(Locale.class)
					.orElseGet(Locale::getDefault);
		} catch (RuntimeException ex)
		{
			return Locale.getDefault();
		}
	}

	public static String get(String key)
	{
		return get("gate.i18n.messages", getLocale(), key);
	}

	public static String get(Locale locale, String key)
	{
		return get("gate.i18n.messages", locale, key);
	}

	public static String get(String context, String key)
	{
		return get(context, getLocale(), key);
	}

	public static String get(String context, Locale locale, String key)
	{
		ResourceBundle bundle = CACHE
				.computeIfAbsent(context, e -> new ConcurrentHashMap<>())
				.computeIfAbsent(locale, e -> ResourceBundle.getBundle(context, e));
		return bundle.getString(key);
	}

	public static String getValue(AnnotatedElement element, Annotation annotation)
	{
		Class<?> owner;
		String key;
		String suffix = "." + annotation.annotationType().getSimpleName().toLowerCase(Locale.ROOT);

		if (element instanceof Class<?> clazz)
		{
			owner = clazz;
			key = "this" + suffix;
		} else if (element instanceof Field field)
		{
			owner = field.getDeclaringClass();
			key = field.getName() + suffix;
		} else if (element instanceof Method method)
		{
			owner = method.getDeclaringClass();
			key = method.getName() + "()" + suffix;
		} else
		{
			throw new IllegalArgumentException("Unsupported element: " + element);
		}

		return get("%s.%s".formatted(owner.getPackageName(), localName(owner)), getLocale(), key);
	}

	private static String localName(Class<?> owner)
	{
		String name = owner.getName();
		return name.substring(owner.getPackageName().length() + 1);
	}
}