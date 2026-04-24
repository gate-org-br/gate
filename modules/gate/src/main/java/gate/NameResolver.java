package gate;

import java.lang.reflect.Method;

public class NameResolver
{

	public static String screen(Class<?> type)
	{
		String name = type.getSimpleName();
		if (!name.endsWith("Screen"))
			throw new IllegalStateException("Screen class name must end with 'Screen': " + type.getName());

		name = name.substring(0, name.length() - 6);
		Class<?> enclosingClass = type.getEnclosingClass();
		if (enclosingClass == null)
			return name;

		return screen(enclosingClass) + "." + name;
	}


	public static String action(Method method)
	{
		String name = method.getName();
		if (name.startsWith("call") && name.length() > 4)
			return name.substring(4);
		return null;
	}
}