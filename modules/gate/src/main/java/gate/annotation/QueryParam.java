package gate.annotation;

import gate.adapter.converter.Converter;
import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

/**
 * Binds a screen action parameter to a scalar query parameter.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParam
{
	String value();

	class Extractor
	{
		public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
		{
			if (!parameter.isAnnotationPresent(QueryParam.class)
			    && !parameter.isAnnotationPresent(jakarta.ws.rs.QueryParam.class))
				throw new BadRequestException("Missing parameter name");

			String name = getName(parameter);
			Object value = request.getParameterValue(name);
			if (DefaultValue.Extractor.isPresent(parameter)
			    && (value == null || value instanceof String s && s.isBlank()))
				value = DefaultValue.Extractor.extract(parameter);

			try
			{
				if (value instanceof Part part)
				{
					try
					{
						value = gate.adapter.handler.Handler.fromPart(parameter.getType(), part);
					} finally
					{
						part.delete();
					}
				} else if (value instanceof String string)
					value = Converter.getConverter(parameter).ofString(parameter.getParameterizedType(), string);

				if (value == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required parameter: " + name);
				return value;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for parameter " + name + ": " + value);
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}

		public static String getName(Parameter parameter)
		{
			return parameter.isAnnotationPresent(QueryParam.class)
					? parameter.getAnnotation(QueryParam.class).value()
					: parameter.getAnnotation(jakarta.ws.rs.QueryParam.class).value();
		}
	}
}