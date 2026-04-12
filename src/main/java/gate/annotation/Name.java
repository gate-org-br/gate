package gate.annotation;

import gate.i18n.I18N;
import gate.util.Reflection;
import jakarta.enterprise.util.Nonbinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

/**
 * Declares a human-readable name for a type, field, method or parameter.
 * <p>
 * When {@link #value()} is not blank, that literal value is used directly. When it is blank, the
 * name is resolved from the i18n metadata bundle associated with the annotated element through
 * {@link I18N#getValue(AnnotatedElement, java.lang.annotation.Annotation)}.
 */
@Info
@Retention(RetentionPolicy.RUNTIME)
@Target(
		{
				ElementType.TYPE, ElementType.FIELD,
				ElementType.METHOD, ElementType.PARAMETER
		})
public @interface Name
{

	/**
	 * Literal name for the annotated element.
	 * <p>
	 * If left blank, the extractor resolves the value from i18n using the metadata key conventions:
	 * {@code this.name} for classes, {@code fieldName.name} for fields and
	 * {@code methodName().name} for methods.
	 *
	 * @return literal name or blank to resolve from i18n
	 */
	@Nonbinding
	String value() default "";

	/**
	 * Resolves names from annotated metadata, reflected references and literals.
	 */
	class Extractor
	{

		/**
		 * Resolves a display name from the informed element.
		 * <p>
		 * Supported inputs are:
		 * <p>
		 * - {@link String}: treated first as a reflective reference accepted by
		 * {@link Reflection#find(String)}; if not resolved, the string itself is returned as a
		 * literal.
		 * <p>
		 * - {@link AnnotatedElement}: resolved from {@link Name}, {@link CopyName} or
		 * {@link CopyInfo}.
		 * <p>
		 * - {@link Enum}: resolved from the enum constant field metadata.
		 * <p>
		 * - any other non-null object: resolved from its runtime class.
		 *
		 * @param element source element, reference string or literal
		 * @return resolved name when available
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
					if (annotatedElement.isAnnotationPresent(Name.class))
					{
						var annotation = annotatedElement.getAnnotation(Name.class);
						var value = annotation.value();
						return Optional.of(value.isBlank() ?
								I18N.getValue(annotatedElement, annotation) : value);
					}
					if (annotatedElement.isAnnotationPresent(CopyName.class))
						return extract(CopyName.Extractor.extract(annotatedElement));
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
