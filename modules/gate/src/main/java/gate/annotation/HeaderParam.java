package gate.annotation;

import gate.adapter.converter.Converter;
import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

/**
 * Binds a screen action parameter to a request header.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderParam
{
	String value();

	class Extractor
	{
		public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
		{
			if (!parameter.isAnnotationPresent(HeaderParam.class)
			    && !parameter.isAnnotationPresent(jakarta.ws.rs.HeaderParam.class))
				throw new BadRequestException("Missing parameter name");

			String name = parameter.isAnnotationPresent(HeaderParam.class)
					? parameter.getAnnotation(HeaderParam.class).value()
					: parameter.getAnnotation(jakarta.ws.rs.HeaderParam.class).value();

			String header = request.getHeader(name);
			if (DefaultValue.Extractor.isPresent(parameter) && (header == null || header.isBlank()))
				header = DefaultValue.Extractor.extract(parameter);

			Converter converter = Converter.getConverter(parameter);

			try
			{
				Object value = converter.ofString(parameter.getParameterizedType(), header);
				if (value == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required parameter: " + name);
				return value;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for parameter " + name + ": " + header);
			}
		}
	}
}
