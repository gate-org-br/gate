package gate.annotation;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethod
{
	class Extractor
	{
		public static List<String> extract(Method method)
		{
			return Arrays.stream(method.getAnnotations())
					.map(Annotation::annotationType)
					.filter(e -> e.isAnnotationPresent(HttpMethod.class))
					.map(Class::getSimpleName)
					.map(String::toUpperCase)
					.toList();
		}
	}
}