package gate.annotation;

import gate.base.Screen;
import gate.catcher.ObjectCatcher;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		java.lang.annotation.ElementType.TYPE,
		java.lang.annotation.ElementType.METHOD,
	})
public @interface Catcher
{

	Class<? extends gate.catcher.Catcher> value();

	class Extractor
	{

		public static Class<? extends gate.catcher.Catcher> extract(Method method) throws ReflectiveOperationException
		{
			if (method.isAnnotationPresent(Catcher.class))
				return method.getAnnotation(Catcher.class).value();

			for (Class<?> type = method.getDeclaringClass();
				Screen.class.isAssignableFrom(type);
				type = type.getSuperclass())
				if (type.isAnnotationPresent(Catcher.class))
					return type.getAnnotation(Catcher.class).value();

			for (Class<?> type = method.getReturnType();
				type != null; type = type.getSuperclass())
				if (type.isAnnotationPresent(Catcher.class))
					return type.getAnnotation(Catcher.class).value();

			return ObjectCatcher.class;
		}
	}
}
