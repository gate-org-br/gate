package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import javax.enterprise.util.Nonbinding;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD, ElementType.PARAMETER
	})
public @interface Code
{

	@Nonbinding
	String value();

	class Extractor
	{

		public static Optional<String> extract(Object element)
		{
			try
			{
				if (element instanceof AnnotatedElement)
				{
					AnnotatedElement annotatedElement = (AnnotatedElement) element;
					if (annotatedElement.isAnnotationPresent(Code.class))
						Optional.of(annotatedElement.getAnnotation(Code.class).value());
					if (annotatedElement.isAnnotationPresent(CopyCode.class))
						return extract(annotatedElement.getAnnotation(CopyCode.class).value());
					if (annotatedElement.isAnnotationPresent(Copy.class))
						return extract(annotatedElement.getAnnotation(Copy.class).value());
					return Optional.empty();
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));
				if (element != null)
					return extract(element.getClass());

				return Optional.empty();

			} catch (NoSuchFieldException ex)
			{
				return Optional.empty();
			}
		}
	}
}
