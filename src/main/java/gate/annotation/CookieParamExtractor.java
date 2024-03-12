package gate.annotation;

import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.stream.Stream;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;

public class CookieParamExtractor
{

	public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
	{
		if (!parameter.isAnnotationPresent(CookieParam.class))
			throw new BadRequestException("Missing parameter name");

		String name = parameter.getAnnotation(CookieParam.class).value();

		String cookie = Stream.of(request.getCookies())
			.filter(e -> Objects.equals(name, e.getName()))
			.map(e -> e.getValue())
			.findFirst().orElse(null);

		if (parameter.isAnnotationPresent(DefaultValue.class) && (cookie == null || cookie.isBlank()))
			cookie = parameter.getAnnotation(DefaultValue.class).value();

		gate.converter.Converter converter = gate.converter.Converter.getConverter(parameter);

		try
		{
			Object value = converter.ofString(parameter.getType(), cookie);
			if (value == null && parameter.isAnnotationPresent(Required.class))
				throw new BadRequestException("Missing required parameter: " + name);
			return value;
		} catch (ConversionException ex)
		{
			throw new BadRequestException("Invalid value for parameter " + name + ": " + cookie);
		}
	}
}
