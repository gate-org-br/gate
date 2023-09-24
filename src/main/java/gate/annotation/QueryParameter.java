package gate.annotation;

import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import javax.servlet.http.Part;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParameter
{

	String value();

	public static class Extractor
	{

		public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
		{
			String name = null;
			if (parameter.isAnnotationPresent(QueryParameter.class))
				name = parameter.getAnnotation(QueryParameter.class).value();
			else if (parameter.isNamePresent())
				name = parameter.getName();
			else
				throw new BadRequestException("Missing parameter name");

			Object value = request.getParameterValue(name);

			gate.converter.Converter converter
				= gate.converter.Converter.getConverter(parameter);

			try
			{
				if (value instanceof Part)
				{
					Part part = (Part) value;

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
				} else if (value instanceof String)
					value = converter.ofString(parameter.getType(), (String) value);

				if (value == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required parameter: " + name);
				return value;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for parameter " + name + ": " + value);
			}
		}
	}
}
