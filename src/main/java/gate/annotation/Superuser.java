package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that only a superuser can access the resource.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE
		})
public @interface Superuser
{

}