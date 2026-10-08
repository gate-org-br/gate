package gate.adapter.handler;

import gate.Progress;
import gate.adapter.converter.Converter;
import gate.adapter.registry.HandlerRegistry;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.util.Reflection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface Handler
{
	/**
	 * Gets the handler associated with the specified java class.
	 *
	 * @param type java class whose associated handler must be returned
	 * @return the handler associated with the specified java class
	 */
	static Class<? extends Handler> getHandler(Type type)
	{
		return HandlerRegistry.INSTANCE.get(Reflection.getRawType(type));
	}

	void handle(HttpServletRequest request, HttpServletResponse response, Object value);

	default Object ofPart(Type type, Part part) throws ConversionException
	{
		throw new UnsupportedOperationException("This type can't be converted from a Part.");
	}

	default void handle(HttpServletRequest request, Progress progress, Object value)
	{
		progress.result("application/octet-stream", null, Converter.toString(value));
	}

	@SuppressWarnings("unchecked")
	static <T> T fromPart(Type type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		try
		{
			return (T) getHandler(Reflection.getRawType(type))
					.getDeclaredConstructor().newInstance()
					.ofPart(type, part);
		} catch (ReflectiveOperationException ex)
		{
			throw new AppError(ex);
		}
	}

	static Class<? extends Handler> getHandler(Method method, Object result)
	{
		return method.isAnnotationPresent(gate.annotation.Handler.class)
				? method.getAnnotation(gate.annotation.Handler.class).value()
				: Handler.getHandler(result.getClass());
	}
}