package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the type of security to apply
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE
		})
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
		SUPERUSER,
		/**
		 * Blocks access to everyone
		 */
		BLOCK
	}
}