package gate.annotation;

import gate.constraint.Required;
import gate.error.ConversionException;
import gate.error.BadRequestException;
import gate.util.Toolkit;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParameter
{

	int value();

	public static class Extractor
	{

		public static Object extract(HttpServletRequest request, Parameter parameter) throws BadRequestException
		{
			String module = request.getParameter("MODULE");
			String screen = request.getParameter("SCREEN");
			String action = request.getParameter("ACTION");
			List<String> path = Toolkit.parsePath(request.getPathInfo());
			int index = parameter.getAnnotation(PathParameter.class).value();
			int offset = index + (Toolkit.isEmpty(module, screen, action) ? 3 : 0);

			if (path.size() > offset)
			{
				String string = path.get(offset);

				gate.converter.Converter converter = gate.converter.Converter.getConverter(parameter);

				try
				{
					Object value = converter.ofString(parameter.getType(), string);
					if (value == null && parameter.isAnnotationPresent(Required.class))
						throw new BadRequestException("Missing required parameter: " + index);
					return value;
				} catch (ConversionException ex)
				{
					throw new BadRequestException("Invalid value for parameter " + index + ": " + string);
				}
			}

			if (parameter.isAnnotationPresent(Required.class))
				throw new BadRequestException("Missing required parameter: " + index);
			return null;
		}
	}

}
