package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Info
@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.FIELD, ElementType.METHOD
		})
public @interface Metadata
{
	Class<? extends gate.adapter.metadata.Metadata> value();

	class Extractor
	{
		public static gate.adapter.metadata.Metadata extract(AnnotatedElement element)
		{
			try
			{
				return element.isAnnotationPresent(gate.annotation.Metadata.class)
						? element.getDeclaredAnnotation(gate.annotation.Metadata.class)
						  .value().getConstructor().newInstance()
						: gate.adapter.metadata.Metadata.EMPTY;
			} catch (ReflectiveOperationException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
}