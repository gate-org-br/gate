package gate.constraint;

import gate.converter.Converter;
import gate.error.AppException;
import gate.lang.property.Property;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Implementation(Minlength.Implementation.class)
public @interface Minlength
{

	int value();

	public static class Implementation extends Constraint.Implementation
	{

		private static final long serialVersionUID = 1L;

		public Implementation(Object value)
		{
			super(value);
		}

		@Override
		public void validate(Object entity, Property property) throws AppException
		{
			Integer constraint = (Integer) getValue();
			Class type = property.getRawType();
			String value = Converter
					.getConverter(type)
					.toString(type, property.getValue(entity));
			if (value.length() < constraint)
				throw new AppException("O campo %s deve possuir no mínimo %d caracteres.",
						property.getName().orElse(property.toString()),
						getValue());
		}

		@Override
		public String getName()
		{
			return "data-minlength";
		}
	}
}
