package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Placeholder
{

	String value();

	class Extractor
	{

		public static Optional<String> extract(Object element)
		{
			if (element instanceof AnnotatedElement annotatedElement
					&& annotatedElement.isAnnotationPresent(Placeholder.class))
				return Optional.of(annotatedElement.getAnnotation(Placeholder.class).value());

			return Optional.empty();
		}
	}
}
