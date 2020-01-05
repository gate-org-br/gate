package gate.annotation;

import gate.util.Icons;
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
public @interface Icon
{

	String value();

	class Extractor
	{

		public static Optional<Icons.Icon> extract(Object element)
		{
			try
			{
				if (element instanceof String)
				{
					String string = (String) element;

					Optional<Icons.Icon> icon = Icons.getInstance().get(string);
					if (icon.isPresent())
						return icon;

					Optional<? extends AnnotatedElement> optional = Reflection.find(string);
					return optional.isPresent() ? extract(optional.get()) : Optional.empty();
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));

				if (element instanceof AnnotatedElement)
				{
					AnnotatedElement annotatedElement = (AnnotatedElement) element;
					if (annotatedElement.isAnnotationPresent(Icon.class))
						return extract(annotatedElement.getAnnotation(Icon.class).value());
					if (annotatedElement.isAnnotationPresent(CopyIcon.class))
						return extract(CopyIcon.Extractor.extract(annotatedElement));
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
