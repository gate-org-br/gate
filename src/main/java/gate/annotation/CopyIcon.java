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
public @interface CopyIcon
{

	Class<?> value();

	String field() default "";

	class Extractor
	{

		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyIcon copyIcon
				= element.getAnnotation(CopyIcon.class);

			Class<?> type = copyIcon.value();

			if (!copyIcon.field().isEmpty())
				return type.getField(copyIcon.field());

			return type;
		}
	}
}
