package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the target {@link gate.base.Screen} action method to be executed by the Gate framework as a background process
 * handled by a JSP page
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Background
{

	/**
	 * Define the JSP page that will handle the background process user interaction
	 *
	 * @return the JSP page that will handle the background process user interaction
	 */
	String value() default "/WEB-INF/views/Progress.jsp";
}
