package gate.annotation;

import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import jakarta.ws.rs.DefaultValue;
import java.lang.reflect.Parameter;

public class BodyParamExtractor
{

	public static Object extract(ScreenServletRequest request, Parameter parameter)
		throws BadRequestException
	{
		String body = request.getBody();
		if (parameter.isAnnotationPresent(DefaultValue.class)
			&& (body == null || body.isBlank()))
			body = parameter.getAnnotation(DefaultValue.class).value();

		gate.converter.Converter converter
			= gate.converter.Converter.getConverter(parameter);

		try
		{
			Object value = converter.ofString(parameter.getType(), body);
			if (value == null && parameter.isAnnotationPresent(Required.class))
				throw new BadRequestException("Missing required body paramter");
			return value;
		} catch (ConversionException ex)
		{
			throw new BadRequestException("Invalid value for body paramter: " + body);
		}
	}
}
