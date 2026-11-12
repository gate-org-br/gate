package gate.annotation;

import gate.error.AppError;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER
		})
public @interface MapCollector
{
	Class<? extends gate.adapter.mapCollector.MapCollector> value();

	class Extractor
	{
		public static gate.adapter.mapCollector.MapCollector extract(AnnotatedElement element)
		{
			try
			{
				return element.getAnnotation(MapCollector.class).value()
						.getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException ex)
			{
				throw new AppError(ex);
			}
		}

		public static gate.adapter.mapCollector.MapCollector extract(Parameter parameter)
		{
			return extract((AnnotatedElement) parameter);
		}
	}
}
