package gate.annotation;

import gate.util.Reflection;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD
	})
public @interface Description
{

	String value();

	public static class Extractor
	{

		public static Optional<String> extract(Object element)
		{
			try
			{
				if (element instanceof String)
				{
					String string = (String) element;
					Optional<? extends AnnotatedElement> optional = Reflection.find(string);
					return optional.isPresent() ? extract(optional.get()) : Optional.of(string);
				}

				if (element instanceof AnnotatedElement)
					return ((AnnotatedElement) element).isAnnotationPresent(Description.class)
						? extract(((AnnotatedElement) element).getAnnotation(Description.class).value())
						: Optional.empty();

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));

				if (element instanceof Object)
					return extract(element.getClass());

				return Optional.empty();

			} catch (ClassNotFoundException | NoSuchFieldException ex)
			{
				return Optional.empty();
			}
		}
	}
}
