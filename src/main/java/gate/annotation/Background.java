package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the target {@link gate.base.Screen} action method to be executed by the Gate framework as a background process
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Background
{

	String value() default "/WEB-INF/views/Progress.jsp";
}
