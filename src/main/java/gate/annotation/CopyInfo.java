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
public @interface CopyInfo
{

	Class<?> value();

	String field() default "";

	class Extractor
	{

		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyInfo copyInformation
				= element.getAnnotation(CopyInfo.class);

			Class<?> type = copyInformation.value();

			if (!copyInformation.field().isEmpty())
				return type.getField(copyInformation.field());

			return type;
		}
	}
}
