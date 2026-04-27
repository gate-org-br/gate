package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Replaces the authorization coordinates resolved from package, screen, and action conventions.
 *
 * <p>The annotation may be declared on a package, type, or method. Resolution precedence is
 * method, then type, then package.
 *
 * <p>When present, this annotation takes precedence over {@link Module}, {@link Screen}, and
 * {@link Action}, and replaces the derived authorization mapping as a whole. Blank attributes are
 * converted to {@code null}; they do not fall back to the original {@code module},
 * {@code screen}, or {@code action} values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE})
public @interface Authorization
{

	String module() default "";

	String screen() default "";

	String action() default "";

	class Extractor
	{

		/**
		 * Resolves the authorization mapping for the given method.
		 *
		 * <p>If an {@link Authorization} annotation is found on the method, its declaring type, or
		 * the declaring package, the extracted value replaces the original mapping. Otherwise, the
		 * mapping is resolved incrementally from {@link Module}, {@link Screen}, and {@link Action}
		 * annotations before falling back to the provided {@code module}, {@code screen}, and
		 * {@code action} values.
		 */
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

			if (pack.isAnnotationPresent(Module.class))
				module = pack.getAnnotation(Module.class).value();

			if (type.isAnnotationPresent(Module.class))
				module = type.getAnnotation(Module.class).value();
			if (type.isAnnotationPresent(Screen.class))
				screen = type.getAnnotation(Screen.class).value();

			if (method.isAnnotationPresent(Module.class))
				module = method.getAnnotation(Module.class).value();
			if (method.isAnnotationPresent(Screen.class))
				screen = method.getAnnotation(Screen.class).value();
			if (method.isAnnotationPresent(Action.class))
				action = method.getAnnotation(Action.class).value();

			return new Value(module, screen, action);
		}
	}

	record Value(String module, String screen, String action)
	{
		public Value
		{
			if (module != null && module.isBlank())
				module = null;
			if (screen != null && screen.isBlank())
				screen = null;
			if (action != null && action.isBlank())
				action = null;
		}

		/**
		 * Creates a value from an {@link Authorization} annotation.
		 *
		 * <p>Blank annotation attributes are normalized to {@code null}.
		 */
		public Value(Authorization annotation)
		{
			this(annotation.module(),
					annotation.screen(),
					annotation.action());
		}
	}
}
