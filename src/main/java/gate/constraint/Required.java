package gate.constraint;

import gate.error.AppException;
import gate.lang.property.Property;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint
@Target(
		{
			ElementType.FIELD, ElementType.PARAMETER
		})
@Retention(RetentionPolicy.RUNTIME)
@Implementation(Required.Implementation.class)
public @interface Required
{

	String value() default "required";

	class Implementation extends Constraint.Implementation<String>
	{

		private static final long serialVersionUID = 1L;

		public Implementation(Object value)
		{
			super("required");
		}

		@Override
		public void validate(Object entity, Property property) throws AppException
		{
			if (getValue().equals("required"))
			{
				var value = property.getValue(entity);
				if (value == null
						|| (value instanceof String string && string.isBlank()))
				{
					String name = property.getDisplayName();
					if (name == null)
						name = property.toString();
					throw new AppException(String.format("O campo %s é requerido.", name));
				}
			}
		}

		@Override
		public String getName()
		{
			return "required";
		}
	}
}
