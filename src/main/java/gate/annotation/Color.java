package gate.annotation;

import gate.type.Colored;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD, ElementType.METHOD
	})
public @interface Color
{

	String value();

	public static class Extractor
	{

		public static Optional<String> extract(AnnotatedElement element)
		{
			return element.isAnnotationPresent(Color.class)
				? Optional.of(element.getAnnotation(Color.class).value()) : Optional.empty();
		}

		public static Optional<String> extract(Object element)
		{
			try
			{
				if (element instanceof Colored)
					return Optional.of(((Colored) element).getColor());

				if (element instanceof String)
				{
					String string = (String) element;
					if (!string.contains(":"))
						return extract(Class.forName(string));

					String[] strings = string.split(":");

					if (strings[1].endsWith("()"))
						return extract(Class.forName(strings[0]).getMethod(strings[1].substring(0, strings[1].length() - 2)));

					return extract(Class.forName(strings[0]).getField(strings[1]));
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));

				if (element instanceof AnnotatedElement)
					return extract((AnnotatedElement) element);

				if (element instanceof Object)
					return extract(element.getClass());

			} catch (ClassNotFoundException
				| NoSuchMethodException
				| NoSuchFieldException ex)
			{
				Logger.getGlobal().log(Level.SEVERE, "Error loading name", ex);
			}

			return Optional.empty();
		}
	}
}
