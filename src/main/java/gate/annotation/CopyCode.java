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
public @interface CopyCode
{

	Class<?> value();

	String field() default "";

	class Extractor
	{

		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyCode copyCode
				= element.getAnnotation(CopyCode.class);

			Class<?> type = copyCode.value();

			if (!copyCode.field().isEmpty())
				return type.getField(copyCode.field());

			return type;
		}
	}
}
