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
 * Skip authorization and grants access to all authenticated users
 */
public @interface AuthWithAuthentication
{

}
