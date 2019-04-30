package gate.annotation;

import gate.util.Icons;
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
		ElementType.TYPE, ElementType.FIELD, ElementType.METHOD
	})
public @interface Icon
{

	String value();

	public static class Extractor
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
					return ((AnnotatedElement) element).isAnnotationPresent(Icon.class)
						? extract(((AnnotatedElement) element).getAnnotation(Icon.class).value())
						: Optional.empty();

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
