package gate.annotation;

import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import java.lang.reflect.Parameter;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;

public class HeaderParamExtractor
{

	public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
	{
		if (!parameter.isAnnotationPresent(HeaderParam.class))
			throw new BadRequestException("Missing parameter name");

		String name = parameter.getAnnotation(HeaderParam.class).value();

		String header = request.getHeader(name);
		if (parameter.isAnnotationPresent(DefaultValue.class) && (header == null || header.isBlank()))
			header = parameter.getAnnotation(DefaultValue.class).value();

		gate.converter.Converter converter = gate.converter.Converter.getConverter(parameter);

		try
		{
			Object value = converter.ofString(parameter.getType(), header);
			if (value == null && parameter.isAnnotationPresent(Required.class))
				throw new BadRequestException("Missing required parameter: " + name);
			return value;
		} catch (ConversionException ex)
		{
			throw new BadRequestException("Invalid value for parameter " + name + ": " + header);
		}
	}
}
