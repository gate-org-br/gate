package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD, ElementType.PARAMETER
	})
public @interface CopyTooltip
{

	Class<?> value();

	String field() default "";

	class Extractor
	{

		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyTooltip copyTooltip
				= element.getAnnotation(CopyTooltip.class);

			Class<?> type = copyTooltip.value();

			if (!copyTooltip.field().isEmpty())
				return type.getField(copyTooltip.field());

			return type;
		}
	}
}
