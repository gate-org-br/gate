package gate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Reuses generic info metadata from another type or one of its public fields.
 * <p>
 * This annotation is used as a shared indirection point by metadata extractors such as
 * {@link Name.Extractor} and {@link Description.Extractor}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.TYPE, ElementType.FIELD,
		ElementType.METHOD, ElementType.PARAMETER
	})
public @interface CopyInfo
{

	/**
	 * Type that owns the referenced metadata.
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
	 * Resolves the annotated element referenced by {@link CopyInfo}.
	 */
	class Extractor
	{

		/**
		 * Resolves the target annotated element configured in {@link CopyInfo}.
		 *
		 * @param element element annotated with {@link CopyInfo}
		 * @return referenced type or public field
		 * @throws NoSuchFieldException when the configured field does not exist
		 */
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
