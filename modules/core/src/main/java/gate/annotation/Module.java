package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the authorization module.
 *
 * <p>It may be declared on a package, type, or method. Package values act as the outer default,
 * type values override the package, and method values override both for the current action.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
public @interface Module
{
	String value();
}
