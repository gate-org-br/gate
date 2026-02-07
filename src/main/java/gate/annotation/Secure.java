package gate.annotation;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.*;
import java.lang.annotation.ElementType;

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
