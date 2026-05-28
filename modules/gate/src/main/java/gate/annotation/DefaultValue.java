package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Default value used when a screen action parameter is missing or blank.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue
{
	String value();

	class Extractor
	{
		public static boolean isPresent(java.lang.reflect.Parameter parameter)
		{
			return parameter.isAnnotationPresent(DefaultValue.class)
			       || parameter.isAnnotationPresent(jakarta.ws.rs.DefaultValue.class);
		}

		public static String extract(java.lang.reflect.Parameter parameter)
		{
			if (parameter.isAnnotationPresent(DefaultValue.class))
				return parameter.getAnnotation(DefaultValue.class).value();
			return parameter.getAnnotation(jakarta.ws.rs.DefaultValue.class).value();
		}
	}
}
