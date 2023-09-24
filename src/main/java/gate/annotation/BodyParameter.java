package gate.annotation;

import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BodyParameter
{

	public static class Extractor
	{

		public static Object extract(ScreenServletRequest request, Parameter parameter)
			throws BadRequestException
		{
			String string = request.getBody();
			gate.converter.Converter converter
				= gate.converter.Converter.getConverter(parameter);

			try
			{
				Object value = converter.ofString(parameter.getType(), string);
				if (value == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required body paramter");
				return value;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for body paramter: " + string);
			}
		}
	}
}
