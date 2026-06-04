package gate.annotation;

import gate.error.PropertyError;
import gate.lang.property.FieldAttribute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.FIELD)
public @interface Discriminator
{

	class Extractor
	{
		private Extractor() {}

		public static FieldAttribute extract(Class<?> type)
		{
			var discriminators = Arrays.stream(type.getDeclaredFields())
					.filter(e -> e.isAnnotationPresent(Discriminator.class))
					.toList();

			if (discriminators.isEmpty())
				return null;

			if (discriminators.size() > 1)
				throw new PropertyError("Ambiguous discriminator properties found on %s: %s"
						.formatted(type.getName(), discriminators.stream()
								.map(Field::getName)
								.toList()));

			var discriminator = discriminators.get(0);

			if (!discriminator.getType().isEnum())
				throw new PropertyError("Invalid discriminator property %s on %s"
						.formatted(discriminator.getName(), type.getName()));

			for (var constant : discriminator.getType().getEnumConstants())
			{
				var value = (Enum<?>) constant;
				try
				{
					var field = discriminator.getType().getField(value.name());
					if (!field.isAnnotationPresent(Subtype.class))
						throw new PropertyError("Missing subtype on discriminator constant %s.%s"
								.formatted(discriminator.getName(), value.name()));

					var subtype = field.getAnnotation(Subtype.class).value();
					if (!type.isAssignableFrom(subtype))
						throw new PropertyError("Invalid subtype %s on discriminator constant %s.%s"
								.formatted(subtype.getName(), discriminator.getName(), value.name()));
				} catch (NoSuchFieldException ex)
				{
					throw new PropertyError("Invalid discriminator constant %s.%s"
							.formatted(discriminator.getName(), value.name()));
				}
			}

			return FieldAttribute.of(discriminator);
		}
	}
}