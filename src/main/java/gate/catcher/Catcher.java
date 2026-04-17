package gate.catcher;

import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;

public interface Catcher
{

	void catches(ScreenServletRequest request,
	             ScreenServletResponse response, Throwable exception);

	static Class<? extends Catcher> getCatcher(Class<?> type)
	{
		for (Class<?> clazz = type;
		     clazz != null;
		     clazz = clazz.getSuperclass())
			if (clazz.isAnnotationPresent(gate.annotation.Catcher.class))
				return clazz.getAnnotation(gate.annotation.Catcher.class).value();
		return ThrowableCatcher.class;
	}

}