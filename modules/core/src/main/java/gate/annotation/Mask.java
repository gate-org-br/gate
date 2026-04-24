package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mask
{

	String value();


	class Extractor
	{

		public static Optional<String> extract(Object element)
		{
			if (element instanceof AnnotatedElement annotatedElement
					&& annotatedElement.isAnnotationPresent(Mask.class))
				return Optional.of(annotatedElement.getAnnotation(Mask.class).value());

			return Optional.empty();
		}
	}
}
