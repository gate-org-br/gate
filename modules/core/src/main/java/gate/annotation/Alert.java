package gate.annotation;

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
		ElementType.TYPE, ElementType.FIELD, ElementType.METHOD
	})
public @interface Alert
{

	String value();

	class Extractor
	{

		public static Optional<String> extract(Object element)
		{
			try
			{

				if (element instanceof String)
				{
					String string = (String) element;
					Optional<? extends AnnotatedElement> optional = Reflection.find(string);
					return optional.isPresent() ? extract(optional.get()) : Optional.empty();
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));

				if (element instanceof AnnotatedElement)
				{
					AnnotatedElement annotatedElement = (AnnotatedElement) element;
					if (annotatedElement.isAnnotationPresent(Alert.class))
						return Optional.of(annotatedElement.getAnnotation(Alert.class).value());
					return Optional.empty();
				}

				if (element != null)
					return extract(element.getClass());

			} catch (ClassNotFoundException | NoSuchFieldException ex)
			{
				return Optional.empty();
			}

			return Optional.empty();
		}
	}
}
