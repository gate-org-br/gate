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
public @interface JsonConverter
{

	Class<? extends gate.adapter.jsonConverter.JsonConverter> value();
}