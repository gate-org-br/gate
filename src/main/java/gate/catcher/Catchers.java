package gate.catcher;

import java.lang.reflect.Method;

public class Catchers
{

	public static Catcher get(Method method, Class<? extends Throwable> type) throws ReflectiveOperationException
	{
		var catchers = gate.annotation.Catcher.Extractor.extract(method);
		for (Class<? extends Throwable> clazz = type;
			Throwable.class.isAssignableFrom(clazz);
			clazz = (Class<? extends Throwable>) clazz.getSuperclass())
			if (catchers.containsKey(clazz))
				return catchers.get(clazz).getConstructor().newInstance();
		return null;
	}
}
