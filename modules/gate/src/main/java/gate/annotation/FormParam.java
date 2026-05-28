package gate.annotation;

import gate.adapter.converter.Converter;
import gate.constraint.Required;
import gate.error.BadRequestException;
import gate.error.ConversionException;
import gate.http.ScreenServletRequest;
import gate.lang.property.PropertyGraph;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Binds a screen action parameter to request parameters.
 * <p>
 * Scalar parameters are read directly by name. Complex parameters are populated
 * from prefixed properties such as {@code form.name} and {@code form.role.id}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormParam
{
	String value();

	class Extractor
	{
		public static Object extract(ScreenServletRequest request, Parameter parameter) throws BadRequestException
		{
			if (!parameter.isAnnotationPresent(FormParam.class)
			    && !parameter.isAnnotationPresent(jakarta.ws.rs.FormParam.class))
				throw new BadRequestException("Missing parameter name");

			String name = getName(parameter);

			Object value = request.getParameterValue(name);
			if (DefaultValue.Extractor.isPresent(parameter)
			    && (value == null || value instanceof String s && s.isBlank()))
				value = DefaultValue.Extractor.extract(parameter);

			try
			{
				if (value instanceof Part part)
				{
					try
					{
						return gate.adapter.handler.Handler.fromPart(parameter.getType(), part);
					} finally
					{
						part.delete();
					}
				}
				if (value instanceof String string)
					return convert(parameter, name, string);

				var properties = getPrefixedProperties(request, name);
				if (!properties.isEmpty())
					return PropertyGraph.of(parameter.getType(), properties)
							.populate(null, property -> request.getParameter(property.getRawType(),
									name + "." + property));

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

		public static String getName(Parameter parameter)
		{
			if (parameter.isAnnotationPresent(FormParam.class))
				return parameter.getAnnotation(FormParam.class).value();
			return parameter.getAnnotation(jakarta.ws.rs.FormParam.class).value();
		}

		private static Object convert(Parameter parameter, String name, String value)
		{
			try
			{
				Object result = Converter.getConverter(parameter).ofString(parameter.getParameterizedType(), value);
				if (result == null && parameter.isAnnotationPresent(Required.class))
					throw new BadRequestException("Missing required parameter: " + name);
				return result;
			} catch (ConversionException ex)
			{
				throw new BadRequestException("Invalid value for parameter " + name + ": " + value);
			}
		}

		private static List<String> getPrefixedProperties(ScreenServletRequest request, String name)
		{
			List<String> properties = new ArrayList<>();
			String dotPrefix = name + ".";

			for (String parameter : request.getParameterList())
				if (parameter.startsWith(dotPrefix))
					properties.add(parameter.substring(dotPrefix.length()));

			return properties.stream().sorted().toList();
		}
	}
}