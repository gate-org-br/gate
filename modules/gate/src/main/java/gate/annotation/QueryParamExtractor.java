package gate.annotation;

import gate.adapter.converter.Converter;
import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Parameter;

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
				value = Converter.getConverter(parameter).ofString(parameter.getType(), string);

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
}