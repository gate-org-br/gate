package gate.i18n;

import java.util.Locale;

public final class CurrentLocale
{
	private static final ThreadLocal<Locale> CURRENT = new ThreadLocal<>();

	public static Locale get()
	{
		Locale locale = CURRENT.get();
		return locale != null
				? locale : Locale.getDefault();
	}

	public static void set(Locale locale)
	{
		CURRENT.set(locale);
	}

	public static void clear()
	{
		CURRENT.remove();
	}
}