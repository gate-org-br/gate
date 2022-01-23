package gate.annotation;

import gate.util.Emojis;
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
public @interface Emoji
{

	String value();

	static final Pattern PATTERN = Pattern.compile("^[A-F0-9]+$", Pattern.CASE_INSENSITIVE);

	class Extractor
	{

		public static Optional<Emojis.Emoji> extract(Object element)
		{
			try
			{
				if (element instanceof String)
				{
					String string = (String) element;
					if (PATTERN.matcher(string).matches())
						return Emojis.getInstance().get(string);

					Optional<? extends AnnotatedElement> optional = Reflection.find(string);
					return optional.isPresent() ? extract(optional.get()) : Optional.empty();
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));

				if (element instanceof AnnotatedElement)
				{
					AnnotatedElement annotatedElement = (AnnotatedElement) element;
					if (annotatedElement.isAnnotationPresent(Emoji.class))
						return extract(annotatedElement.getAnnotation(Emoji.class).value());
					if (annotatedElement.isAnnotationPresent(CopyEmoji.class))
						return extract(CopyEmoji.Extractor.extract(annotatedElement));
					if (annotatedElement.isAnnotationPresent(CopyInfo.class))
						return extract(CopyInfo.Extractor.extract(annotatedElement));
					return Optional.empty();
				}

				if (element != null)
					return extract(element.getClass());

				return Optional.empty();
			} catch (ClassNotFoundException | NoSuchFieldException ex)
			{
				return Optional.empty();
			}
		}
	}
}
