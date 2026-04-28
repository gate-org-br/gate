package gate.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Info
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(
		{
				java.lang.annotation.ElementType.TYPE,
				java.lang.annotation.ElementType.FIELD,
				java.lang.annotation.ElementType.PARAMETER,
				java.lang.annotation.ElementType.METHOD
		})
public @interface Adapter
{
	Class<?> value();

	class Extractor
	{
		public static Optional<?> extract(AnnotatedElement element)
		{
			if (!element.isAnnotationPresent(Adapter.class))
				return Optional.empty();
			Class<?> type = element.getDeclaredAnnotation(Adapter.class).value();

			try
			{
				return Optional.of(type.getDeclaredConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException
			         | InvocationTargetException | NoSuchMethodException ex)
			{
				throw new RuntimeException(ex);
			}
		}

		public static <T> Optional<T> extract(AnnotatedElement element, Class<T> contract)
		{
			if (!element.isAnnotationPresent(Adapter.class))
				return Optional.empty();

			Class<?> type = element.getDeclaredAnnotation(Adapter.class).value();
			if (!contract.isAssignableFrom(type))
				return Optional.empty();

			try
			{
				return Optional.of(contract.cast(type.getDeclaredConstructor().newInstance()));
			} catch (InstantiationException | IllegalAccessException
			         | InvocationTargetException | NoSuchMethodException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
}