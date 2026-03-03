package gate.annotation;

import gate.type.PropertyReference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity
{

	String value() default "id";

	public static class Extractor
	{
		private static final Map<Class<?>, PropertyReference<?, ?>> CACHE =
				new ConcurrentHashMap<>();

		Extractor() {}

		@SuppressWarnings("unchecked")
		public static PropertyReference<Object, Object> get(Class<?> type)
		{
			return (PropertyReference<Object, Object>) CACHE.computeIfAbsent(type, k ->
			{
				Entity annotation = k.getAnnotation(Entity.class);
				if (annotation == null)
					return null;
				var id = annotation.value();
				String getter =
						type.isRecord()
								? id
								: "get" + Character.toUpperCase(id.charAt(0)) + id.substring(1);
				try
				{
					Method method = k.getMethod(getter);
					return PropertyReference.of(method);
				} catch (Throwable e)
				{
					throw new IllegalArgumentException(k.getName() + " is not a valid entity");
				}
			});
		}

		public static Object extract(Object object)
		{
			if (object == null)
				return null;

			var ref = get(object.getClass());
			if (ref == null)
				return null;

			return ref.apply(object);
		}
	}
}
