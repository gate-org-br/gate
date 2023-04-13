package gate.catcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Catcher
{

	public void catches(HttpServletRequest request,
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
