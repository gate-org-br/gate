package gate.annotation;

import gate.i18n.I18N;
import gate.util.Reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

/**
 * Declares a human-readable description for a type, field or method.
 * <p>
 * When {@link #value()} is not blank, that literal value is used directly. When it is blank, the
 * description is resolved from the i18n metadata bundle associated with the annotated element
 * through {@link I18N#getValue(AnnotatedElement, java.lang.annotation.Annotation)}.
 */
@Info
@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.FIELD,
				ElementType.METHOD
		})
public @interface Description
{
	/**
	 * Literal description for the annotated element.
	 * <p>
	 * If left blank, the extractor resolves the value from i18n using the metadata key conventions:
	 * {@code this.description} for classes, {@code fieldName.description} for fields and
	 * {@code methodName().description} for methods.
	 *
	 * @return literal description or blank to resolve from i18n
	 */
	String value() default "";

	/**
	 * Resolves descriptions from annotated metadata, reflected references and literals.
	 */
	class Extractor
	{

		/**
		 * Resolves a description from the informed element.
		 * <p>
		 * Supported inputs are:
		 * <p>
		 * - {@link String}: treated first as a reflective reference accepted by
		 * {@link Reflection#find(String)}; if not resolved, the string itself is returned as a
		 * literal.
		 * <p>
		 * - {@link AnnotatedElement}: resolved from {@link Description}, {@link CopyDescription} or
		 * {@link CopyInfo}.
		 * <p>
		 * - {@link Enum}: resolved from the enum constant field metadata.
		 * <p>
		 * - any other non-null object: resolved from its runtime class.
		 *
		 * @param element source element, reference string or literal
		 * @return resolved description when available
		 */
		public static Optional<String> extract(Object element)
		{
			try
			{
				if (element instanceof String)
				{
					Optional<? extends AnnotatedElement> optional = Reflection.find((String) element);
					return optional.isPresent() ? extract(optional.get()) : Optional.of((String) element);
				}

				if (element instanceof AnnotatedElement annotatedElement)
				{
					if (annotatedElement.isAnnotationPresent(Description.class))
					{
						var annotation = annotatedElement.getAnnotation(Description.class);
						var value = annotation.value();
						return Optional.of(value.isBlank() ?
								I18N.getValue(annotatedElement, annotation) : value);
					}
					if (annotatedElement.isAnnotationPresent(CopyDescription.class))
						return extract(CopyDescription.Extractor.extract(annotatedElement));
					if (annotatedElement.isAnnotationPresent(CopyInfo.class))
						return extract(CopyInfo.Extractor.extract(annotatedElement));
					return Optional.empty();
				}

				if (element instanceof Enum<?>)
					return extract(element.getClass().getField(((Enum<?>) element).name()));
				if (element != null)
					return extract(element.getClass());

				return Optional.empty();

			} catch (NoSuchFieldException | ClassNotFoundException ex)
			{
				return Optional.empty();
			}
		}
	}
}
