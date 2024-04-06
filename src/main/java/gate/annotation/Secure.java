package gate.annotation;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@InterceptorBinding
@Target(
	{
		ElementType.TYPE, ElementType.METHOD
	})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure
{

}
