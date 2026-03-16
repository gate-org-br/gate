package gate.annotation;

import gate.i18n.I18N;
import gate.util.Reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

@Info
@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.FIELD,
				ElementType.METHOD
		})
public @interface Description
{
	String value() default "";

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

				if (element instanceof AnnotatedElement annotatedElement)
				{
					if (annotatedElement.isAnnotationPresent(Description.class))
					{
						var annotation = annotatedElement.getAnnotation(Description.class);
						var value = annotation.value();
						return Optional.of(value.isBlank() ?
								I18N.getValue(annotatedElement, annotation) : value);
					}
					if (annotatedElement.isAnnotationPresent(CopyDescription.class))
						return extract(CopyDescription.Extractor.extract(annotatedElement));
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
