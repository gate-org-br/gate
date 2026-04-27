package gate.handler;

import gate.Progress;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.registrar.Registry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public interface Handler
{
	class Instances
	{
		private static final Registry<Class<? extends Handler>> HANDLERS = Registry
				.create(HandlerRegistrar.class,
						type -> type.isAnnotationPresent(gate.annotation.Handler.class)
								? type.getAnnotation(gate.annotation.Handler.class).value()
								: null, type -> SerializableHandler.class);
	}

	/**
	 * Gets the handler associated with the specified java class.
	 *
	 * @param type java class whose associated handler must be returned
	 * @return the handler associated with the specified java class
	 */
	static Class<? extends Handler> getHandler(Class<?> type)
	{
		return Instances.HANDLERS.get(type);
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
}