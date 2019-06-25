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
@Implementation(Maxlength.Implementation.class)
public @interface Maxlength
{

	int value();

	class Implementation extends Constraint.Implementation
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
			if (value.length() > constraint)
			{
				String name = property.getDisplayName();
				if (name == null)
					name = property.toString();
				throw new AppException("O campo %s deve possuir no máximo %d caracteres.", name, getValue());
			}
		}

		@Override
		public String getName()
		{
			return "maxlength";
		}
	}
}
