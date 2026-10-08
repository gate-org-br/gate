package gate.sql.annotation;

import java.lang.annotation.ElementType;
import java.util.Optional;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Schema
{

	String value();

	final class Extractor
	{
		private Extractor()
		{
		}

		public static Optional<String> getName(Class<?> type)
		{
			return type.isAnnotationPresent(Schema.class)
					? Optional.of(type.getAnnotation(Schema.class).value())
					: Optional.empty();
		}
	}
}
