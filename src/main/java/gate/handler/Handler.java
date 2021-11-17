package gate.handler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public interface Handler
{

	/**
	 * Gets the handler associated with the specified java class.
	 *
	 * @param type java class whose associated handler must be returned
	 *
	 * @return the handler associated with the specified java class
	 */
	static Class<? extends Handler> getHandler(Class<?> type)
	{
		for (Class<?> clazz = type;
			clazz != null;
			clazz = clazz.getSuperclass())
			if (Instances.HANDLERS.containsKey(clazz))
				return Instances.HANDLERS.get(clazz);
			else if (clazz.isAnnotationPresent(gate.annotation.Handler.class))
				return clazz.getAnnotation(gate.annotation.Handler.class).value();
		return SerializableHandler.class;
	}

	void handle(HttpServletRequest request, HttpServletResponse response, Object value);

	public static class Instances
	{

		private static final Map<Class<?>, Class<? extends Handler>> HANDLERS = new HashMap<>();

		static
		{
			HANDLERS.put(String.class, StringHandler.class);
			HANDLERS.put(Object.class, SerializableHandler.class);
			HANDLERS.put(File.class, FileHandler.class);
			HANDLERS.put(Integer.class, IntegerHandler.class);
			HANDLERS.put(Enum.class, EnumHandler.class);
		}
	}
}
