package gate.handler;

import gate.Progress;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public interface Handler
{

	/**
	 * Gets the handler associated with the specified java class.
	 *
	 * @param type java class whose associated handler must be returned
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

	default Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		throw new UnsupportedOperationException("This type can't be converted from a Part.");
	}

	default void handle(HttpServletRequest request, HttpServletResponse response, Progress progress, Object value)
	{
		progress.result("application/octet-stream",
				null, Converter.toString(value));
	}

	static <T> T fromPart(Class<T> type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		try
		{
			return type.cast(getHandler(type).getDeclaredConstructor().newInstance()
					.ofPart(type, part));
		} catch (ReflectiveOperationException ex)
		{
			throw new AppError(ex);
		}
	}

	class Instances
	{
		private static final Map<Class<?>, Class<? extends Handler>> HANDLERS = new ConcurrentHashMap<>()
		{{
			ServiceLoader.load(HandlerRegistrar.class)
					.forEach(registrar -> registrar.register(HANDLERS));
		}};
	}
}
