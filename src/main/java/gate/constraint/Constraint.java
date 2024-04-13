package gate.constraint;

import gate.error.AppError;
import gate.error.AppException;
import gate.lang.property.Property;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint
{

	abstract class Implementation<T> implements Serializable
	{

		private static final long serialVersionUID = 1L;

		private final T value;

		public Implementation(T value)
		{
			this.value = value;
		}

		public T getValue()
		{
			return value;
		}

		public abstract String getName();

		public abstract void validate(Object entity, Property property) throws AppException;

		public static Implementation<?> getImplementation(Annotation constraint)
		{

			try
			{
				var implementationClass = constraint.annotationType()
						.getAnnotation(gate.constraint.Implementation.class).value();
				var implementationConstructor = implementationClass.getConstructor(Object.class);
				return implementationConstructor
						.newInstance(constraint.getClass().getMethod("value").invoke(constraint));
			} catch (ReflectiveOperationException e)
			{
				throw new AppError(e);
			}
		}

	}

}
