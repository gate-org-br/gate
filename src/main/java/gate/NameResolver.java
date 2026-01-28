package gate;

import java.lang.reflect.Method;

public class NameResolver
{

	public static String screen(Class<? extends Object> type)
	{
		String name = type.getSimpleName();

		if (type.getEnclosingClass() != null)
			return type.getEnclosingClass().getSimpleName()
				+ "$"
				+ name.substring(0, name.length() - 6);

		return name.substring(0, name.length() - 6);
	}

	public static String action(Method method)
	{
		String name = method.getName();
		if (name.startsWith("call") && name.length() > 4)
			return name.substring(4);
		return null;
	}
}
