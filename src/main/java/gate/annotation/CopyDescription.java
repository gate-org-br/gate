package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Reuses {@link Description} metadata from another type or one of its public fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD, ElementType.PARAMETER
	})
public @interface CopyDescription
{

	/**
	 * Type that owns the referenced {@link Description} metadata.
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
	 * Resolves the annotated element referenced by {@link CopyDescription}.
	 */
	class Extractor
	{

		/**
		 * Resolves the target annotated element configured in {@link CopyDescription}.
		 *
		 * @param element element annotated with {@link CopyDescription}
		 * @return referenced type or public field
		 * @throws NoSuchFieldException when the configured field does not exist
		 */
		public static AnnotatedElement extract(AnnotatedElement element)
			throws NoSuchFieldException
		{
			CopyDescription copyDescription
				= element.getAnnotation(CopyDescription.class);

			Class<?> type = copyDescription.value();

			if (!copyDescription.field().isEmpty())
				return type.getField(copyDescription.field());

			return type;
		}
	}
}
