package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE
	})
/**
 * Indicates that a resource is public and there is not need of authentication to access it.
 */
public @interface Public
{

}
