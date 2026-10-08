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
 * Binds a screen action parameter to the request body.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BodyParam
{
	class Extractor
	{
		public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
		{
			String body = request.getBody();
			if (DefaultValue.Extractor.isPresent(parameter)
			    && (body == null || body.isBlank()))
				body = DefaultValue.Extractor.extract(parameter);

			Converter converter = Converter.getConverter(parameter);

			try
			{
				Object value = converter.ofString(parameter.getParameterizedType(), body);
				if (value == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required body parameter");
				return value;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for body parameter: " + body);
			}
		}
	}
}
