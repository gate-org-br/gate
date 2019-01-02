package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD
	})
public @interface Color
{

	String value();

	public static class Extractor
	{

		public static final String UNKNOWN = "?";

		public static String extract(AnnotatedElement element)
		{
			return element.isAnnotationPresent(Color.class)
				? element.getAnnotation(Color.class).value() : "?";
		}

		public static String extract(Object element)
		{
			try
			{
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

			return UNKNOWN;
		}
	}
}
