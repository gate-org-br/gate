package gate.annotation;

import gate.type.Colored;
import gate.util.Reflection;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.regex.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD, ElementType.METHOD
	})
public @interface Color
{

	String value();

	class Extractor
	{

		private static final Pattern PATTERN = Pattern.compile("^#[0-9ABCDEFabcdef]{6}$");

		public static Optional<String> extract(Object element)
		{
			try
			{
				if (element instanceof String)
				{
					String string = (String) element;
					if (PATTERN.matcher(string).matches())
						return Optional.of(string);

					Optional<? extends AnnotatedElement> optional = Reflection.find(string);
					return optional.isPresent() ? extract(optional.get()) : Optional.empty();
				}

				if (element instanceof Colored)
					return Optional.of(((Colored) element).getColor());

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));

				if (element instanceof AnnotatedElement)
					return ((AnnotatedElement) element).isAnnotationPresent(Color.class)
						? extract(((AnnotatedElement) element).getAnnotation(Color.class).value())
						: Optional.empty();

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
