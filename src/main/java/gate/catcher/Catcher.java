package gate.catcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Catcher
{

	void catches(HttpServletRequest request,
				 HttpServletResponse response, Throwable exception);

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
