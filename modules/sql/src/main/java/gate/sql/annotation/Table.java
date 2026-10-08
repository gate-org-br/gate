package gate.sql.annotation;

import gate.annotation.Entity;
import gate.error.PropertyError;
import gate.sql.Formatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table
{

	String value();

	final class Extractor
	{
		private Extractor()
		{
		}

		public static String getName(Class<?> type)
		{
			if (!type.isAnnotationPresent(Entity.class))
				throw new PropertyError("%s is not an Entity", type.getName());
			return type.isAnnotationPresent(Table.class) ? type.getAnnotation(Table.class).value()
					: type.getSimpleName();
		}

		public static String getFullName(Class<?> type)
		{
			if (!type.isAnnotationPresent(Entity.class))
				throw new PropertyError("%s is not an Entity", type.getName());
			return Schema.Extractor.getName(type)
					.map(schema -> Formatter.identifier(schema) + "."
					               + Formatter.identifier(getName(type)))
					.orElseGet(() -> Formatter.identifier(getName(type)));
		}
	}
}