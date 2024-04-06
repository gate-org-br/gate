package gate.handler;

import gate.Progress;
import gate.converter.Converter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

		for (Class<?> clazz = type;
			clazz != null;
			clazz = clazz.getSuperclass())
			for (Class<?> inter : type.getInterfaces())
				if (Instances.HANDLERS.containsKey(inter))
					return Instances.HANDLERS.get(inter);
				else if (inter.isAnnotationPresent(gate.annotation.Handler.class))
					return inter.getAnnotation(gate.annotation.Handler.class).value();

		return SerializableHandler.class;
	}

	void handle(HttpServletRequest request, HttpServletResponse response, Object value);

	default void handle(HttpServletRequest request, HttpServletResponse response, Progress progress, Object value)
	{
		progress.result("application/octet-stream",
			null, Converter.toString(value));
	}

	public static class Instances
	{

		private static final Map<Class<?>, Class<? extends Handler>> HANDLERS = new HashMap<>();

		static
		{
			HANDLERS.put(String.class, StringHandler.class);
			HANDLERS.put(File.class, FileHandler.class);
			HANDLERS.put(Integer.class, IntegerHandler.class);
			HANDLERS.put(Enum.class, EnumHandler.class);
			HANDLERS.put(Path.class, PathHandler.class);
		}
	}
}
