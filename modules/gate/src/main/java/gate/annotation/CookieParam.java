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
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Binds a screen action parameter to a request cookie.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CookieParam
{
	String value();

	class Extractor
	{
		public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
		{
			if (!parameter.isAnnotationPresent(CookieParam.class)
			    && !parameter.isAnnotationPresent(jakarta.ws.rs.CookieParam.class))
				throw new BadRequestException("Missing parameter name");

			String name = parameter.isAnnotationPresent(CookieParam.class)
					? parameter.getAnnotation(CookieParam.class).value()
					: parameter.getAnnotation(jakarta.ws.rs.CookieParam.class).value();

			String cookie = Stream.of(request.getCookies())
					.filter(e -> Objects.equals(name, e.getName()))
					.map(e -> e.getValue())
					.findFirst().orElse(null);

			if (DefaultValue.Extractor.isPresent(parameter) && (cookie == null || cookie.isBlank()))
				cookie = DefaultValue.Extractor.extract(parameter);

			Converter converter = Converter.getConverter(parameter);

			try
			{
				Object value = converter.ofString(parameter.getParameterizedType(), cookie);
				if (value == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required parameter: " + name);
				return value;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for parameter " + name + ": " + cookie);
			}
		}
	}
}
