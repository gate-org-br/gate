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
public @interface Collector
{
	Class<? extends gate.adapter.collector.Collector> value();

	class Extractor
	{
		public static gate.adapter.collector.Collector extract(AnnotatedElement element)
		{
			try
			{
				return element.getAnnotation(Collector.class).value()
						.getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException ex)
			{
				throw new AppError(ex);
			}
		}

		public static gate.adapter.collector.Collector extract(Parameter parameter)
		{
			return extract((AnnotatedElement) parameter);
		}
	}
}
