package gate.annotation;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

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
