package gate.constraint;

import gate.adapter.converter.Converter;
import gate.adapter.renderer.Renderer;
import gate.error.AppException;
import gate.lang.property.Property;

import java.io.Serial;
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

		@Serial private static final long serialVersionUID = 1L;

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
				String name = property.getMetadata().name();
				if (name == null)
					name = property.toString();
				throw new AppException(String.format("O campo %s deve ser menor do que %s.", name, Renderer.render(getValue())));
			}

		}

		@Override
		public String getName()
		{
			return "min";
		}
	}
}