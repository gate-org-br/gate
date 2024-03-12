package gate.annotation;

import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Parameter;
import javax.servlet.http.Part;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class QueryParamExtractor
{

	public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
	{

		if (!parameter.isAnnotationPresent(QueryParam.class))
			throw new BadRequestException("Missing parameter name");

		String name = parameter.getAnnotation(QueryParam.class).value();

		Object value = request.getParameterValue(name);
		if (parameter.isAnnotationPresent(DefaultValue.class)
			&& (value == null || value instanceof String s && s.isBlank()))
			value = parameter.getAnnotation(DefaultValue.class).value();

		gate.converter.Converter converter
			= gate.converter.Converter.getConverter(parameter);

		try
		{
			if (value instanceof Part part)
			{
				try
				{
					value = converter.ofPart(parameter.getType(), part);
				} finally
				{
					try
					{
						part.delete();
					} catch (IOException ex)
					{
						throw new UncheckedIOException(ex);
					}
				}
			} else if (value instanceof String string)
				value = converter.ofString(parameter.getType(), string);

			if (value == null && parameter.isAnnotationPresent(Required.class))
				throw new BadRequestException("Missing required parameter: " + name);
			return value;
		} catch (ConversionException ex)
		{
			throw new BadRequestException("Invalid value for parameter " + name + ": " + value);
		}
	}
}
