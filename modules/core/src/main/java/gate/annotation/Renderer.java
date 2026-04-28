package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER
		})
public @interface Renderer
{

	Class<? extends gate.adapter.renderer.Renderer> value();
}