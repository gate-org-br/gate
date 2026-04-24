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

@Info
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
				if (Boolean.TRUE.equals(element))
					return Optional.of("#006600");
				else if (Boolean.FALSE.equals(element))
					return Optional.of("#660000");

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
				{
					AnnotatedElement annotatedElement = (AnnotatedElement) element;
					if (annotatedElement.isAnnotationPresent(Color.class))
						return Optional.of(annotatedElement.getAnnotation(Color.class).value());
					if (annotatedElement.isAnnotationPresent(CopyColor.class))
						return extract(CopyColor.Extractor.extract(annotatedElement));
					if (annotatedElement.isAnnotationPresent(CopyInfo.class))
						return extract(CopyInfo.Extractor.extract(annotatedElement));
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
