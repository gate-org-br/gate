package gate.annotation;

import gate.error.PropertyError;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity
{
	String value() default "id";

	final class Extractor
	{
		private Extractor()
		{
		}

		public static String extract(Class<?> type)
		{
			if (!type.isAnnotationPresent(Entity.class))
				throw new PropertyError("%s is not an Entity", type.getName());
			return type.getAnnotation(Entity.class).value();
		}
	}
}