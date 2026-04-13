package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the authorization screen.
 *
 * <p>It may be declared on a type or method. Method values override the screen inherited from the
 * declaring type or the convention-derived value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Screen
{
	String value();
}
