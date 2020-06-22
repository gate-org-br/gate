package gate.annotation;

import gate.util.Reflection;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import javax.enterprise.util.Nonbinding;

@Info
@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD, ElementType.PARAMETER
	})
public @interface Tooltip
{

	@Nonbinding
	String value();

	class Extractor
	{

		public static Optional<String> extract(Object element)
		{
			try
			{
				if (element instanceof String)
				{
					Optional<? extends AnnotatedElement> optional = Reflection.find((String) element);
					return optional.isPresent() ? extract(optional.get()) : Optional.of((String) element);
				}
				if (element instanceof AnnotatedElement)
				{
					AnnotatedElement annotatedElement = (AnnotatedElement) element;
					if (annotatedElement.isAnnotationPresent(Tooltip.class))
						return Optional.of(annotatedElement.getAnnotation(Tooltip.class).value());
					if (annotatedElement.isAnnotationPresent(CopyTooltip.class))
						return extract(CopyTooltip.Extractor.extract(annotatedElement));
					if (annotatedElement.isAnnotationPresent(CopyInfo.class))
						return extract(CopyInfo.Extractor.extract(annotatedElement));
					return Optional.empty();
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));
				if (element != null)
					return extract(element.getClass());

				return Optional.empty();

			} catch (NoSuchFieldException | ClassNotFoundException ex)
			{
				return Optional.empty();
			}
		}
	}
}
