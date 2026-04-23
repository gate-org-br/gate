package gate.annotation;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE,
		ElementType.METHOD,
		ElementType.FIELD,
		ElementType.PARAMETER})
public @interface LinkResource
{
	@Nonbinding
	String value();
}