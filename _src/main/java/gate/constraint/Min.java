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
@Implementation(Min.Implementation.class)
public @interface Min
{

	double value();

	class Implementation extends Constraint.Implementation<Number>
	{

		private static final long serialVersionUID = 1L;

		public Implementation(Object value)
		{
			super((Double) value);
		}

		@Override
		public void validate(Object entity, Property property) throws AppException
		{
			Object object = property.getValue(entity);
			if (object != null && Converter.toNumber(object).doubleValue() < getValue().doubleValue())
			{
				String name = property.getDisplayName();
				if (name == null)
					name = property.toString();
				throw new AppException("O campo %s deve ser menor do que %s.", name, Converter.toText(getValue()));
			}

		}

		@Override
		public String getName()
		{
			return "min";
		}
	}
}
