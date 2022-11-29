package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Optional;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE
	})
/**
 * Define the type of security to apply
 */
public @interface Security
{

	Type value();

	public enum Type
	{

		/**
		 * Allow access to everyone
		 */
		NONE,
		/**
		 * Allow access to all authenticated users
		 */
		AUTHENTICATION,
		/**
		 * Require access to the specified resource
		 */
		AUTHORIZATION,
		/**
		 * Require specific access to the specified resource
		 */
		SPECIFIC_AUTHORIZATION,
		/**
		 * Allow access to superusers only
		 */
		SUPERUSER
	}

	class Extractor
	{

		public static Optional<Type> extract(Method method)
		{
			if (method.isAnnotationPresent(Security.class))
				return Optional.of(method.getAnnotation(Security.class).value());

			if (method.getDeclaringClass().isAnnotationPresent(Security.class))
				return Optional.of(method.getDeclaringClass().getAnnotation(Security.class).value());

			if (method.getDeclaringClass().getPackage().isAnnotationPresent(Security.class))
				return Optional.of(method.getDeclaringClass().getPackage().getAnnotation(Security.class).value());

			return Optional.empty();

		}
	}
}
