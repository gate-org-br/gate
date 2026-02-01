package gate.i18n;

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

	public static String get(String key)
	{
		return get("gate.i18n.messages", Locale.getDefault(), key);
	}

	public static String get(Locale locale, String key)
	{
		return get("gate.i18n.messages", locale, key);
	}

	public static String get(String context, String key)
	{
		return get(context, Locale.getDefault(), key);
	}

	public static String get(String context, Locale locale, String key)
	{
		ResourceBundle bundle = CACHE
				.computeIfAbsent(context, e -> new ConcurrentHashMap<>())
				.computeIfAbsent(locale, e -> ResourceBundle.getBundle(context, e));
		return bundle.getString(key);
	}
}
