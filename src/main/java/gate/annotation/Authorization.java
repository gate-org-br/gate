package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE
	})
/**
 * Override action name before authorization.
 */
public @interface Authorization
{

	String module() default "";

	String screen() default "";

	String action() default "";

	public static class Extractor
	{

		public static Value extract(Method method, String module, String screen, String action)
		{
			if (method.isAnnotationPresent(Authorization.class))
				return new Value(method.getAnnotation(Authorization.class));

			Class<?> type = method.getDeclaringClass();
			if (type.isAnnotationPresent(Authorization.class))
				return new Value(type.getAnnotation(Authorization.class));

			Package pack = type.getPackage();
			if (pack.isAnnotationPresent(Authorization.class))
				return new Value(pack.getAnnotation(Authorization.class));

			return new Value(module, screen, action);
		}

		public static Value extract(Method method)
		{
			return extract(method, null, null, null);
		}
	}

	public static class Value
	{

		private final String module;
		private final String screen;
		private final String action;

		public Value(String module, String screen, String action)
		{
			this.module = module;
			this.screen = screen;
			this.action = action;
		}

		private Value(Authorization annotation)
		{
			this.module = annotation.module().isBlank() ? null : annotation.module();
			this.screen = annotation.screen().isBlank() ? null : annotation.screen();
			this.action = annotation.action().isBlank() ? null : annotation.action();
		}

		public String module()
		{
			return module;
		}

		public String screen()
		{
			return screen;
		}

		public String action()
		{
			return action;
		}
	}
}
