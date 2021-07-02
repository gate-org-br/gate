package gate.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Repeatable(Catchers.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD,
		java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PARAMETER
	})
public @interface Catcher
{

	Class<? extends Exception> type();

	Class<? extends gate.catcher.Catcher> value();

	class Extractor
	{

		public static Map<Class<? extends Throwable>, Class<? extends gate.catcher.Catcher>>
			extract(Method method)
			throws ReflectiveOperationException
		{
			Map<Class<? extends Throwable>, Class<? extends gate.catcher.Catcher>> catchers = new HashMap<>();

			for (Catcher catcher : method.getAnnotationsByType(Catcher.class))
				catchers.putIfAbsent(catcher.type(), catcher.value());

			Class<?> type = method.getDeclaringClass();
			do
			{
				for (Catcher catcher : type.getAnnotationsByType(Catcher.class))
					catchers.putIfAbsent(catcher.type(), catcher.value());
				type = type.getSuperclass();
			} while (type != null);

			return catchers;
		}
	}
}
