package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Reuses {@link Name} metadata from another type or one of its public fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD, ElementType.PARAMETER
	})
public @interface CopyName
{

	/**
	 * Type that owns the referenced {@link Name} metadata.
	 *
	 * @return referenced type
	 */
	Class<?> value();

	/**
	 * Optional public field of {@link #value()} from where the metadata should be copied.
	 * <p>
	 * When blank, the metadata is copied from the type itself.
	 *
	 * @return referenced public field name or blank
	 */
	String field() default "";

	/**
	 * Resolves the annotated element referenced by {@link CopyName}.
	 */
	class Extractor
	{

		/**
		 * Resolves the target annotated element configured in {@link CopyName}.
		 *
		 * @param element element annotated with {@link CopyName}
		 * @return referenced type or public field
		 * @throws NoSuchFieldException when the configured field does not exist
		 */
		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyName copyName
				= element.getAnnotation(CopyName.class);

			Class<?> type = copyName.value();

			if (!copyName.field().isEmpty())
				return type.getField(copyName.field());

			return type;
		}
	}
}
