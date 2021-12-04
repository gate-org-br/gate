package gate.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target(
	{
		TYPE, METHOD, PARAMETER, FIELD
	})
public @interface Current
{

	public static final AnnotationLiteral<Current> LITERAL = new Literal();

	static class Literal extends AnnotationLiteral<Current> implements Current
	{

		private Literal()
		{

		}
	}

}
