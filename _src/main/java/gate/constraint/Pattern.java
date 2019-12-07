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
@Implementation(Pattern.Implementation.class)
public @interface Pattern
{

	String value();

	class Implementation extends Constraint.Implementation<String>
	{

		private static final long serialVersionUID = 1L;

		public Implementation(Object value)
		{
			super((String) value);
		}

		@Override
		public void validate(Object entity, Property property) throws AppException
		{
			Object object = property.getValue(entity);
			if (object != null && !Converter.toString(object).matches(getValue()))
			{
				String name = property.getDisplayName();
				if (name == null)
					name = property.toString();
				throw new AppException("O campo %s deve estar no formato %s.", name, getValue());
			}

		}

		@Override
		public String getName()
		{
			return "pattern";
		}
	}
}
