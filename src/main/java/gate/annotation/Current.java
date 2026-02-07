package gate.annotation;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, PARAMETER, FIELD})
public @interface Current
{

	public static final AnnotationLiteral<Current> LITERAL = new Literal();

	@SuppressWarnings("all")
	static class Literal extends AnnotationLiteral<Current> implements Current
	{

		private Literal()
		{

		}
	}

}
