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
public @interface CopyEmoji
{

	Class<?> value();

	String field() default "";

	class Extractor
	{

		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyEmoji copyEmoji
				= element.getAnnotation(CopyEmoji.class);

			Class<?> type = copyEmoji.value();

			if (!copyEmoji.field().isEmpty())
				return type.getField(copyEmoji.field());

			return type;
		}
	}
}
