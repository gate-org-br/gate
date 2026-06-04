package gate.annotation;

import gate.error.PropertyError;
import gate.lang.property.FieldAttribute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.FIELD)
public @interface Subtype
{
	Class<?> value();

	class Extractor
	{
		private Extractor() {}

		public static List<? extends Class<?>>
		extract(FieldAttribute discriminator)
		{
			return Arrays.stream(discriminator.getRawType().getEnumConstants())
					.map(Enum.class::cast)
					.map(constant ->
					{
						try
						{
							var field = discriminator.getRawType().getField(constant.name());
							return field.getAnnotation(Subtype.class).value();
						} catch (NoSuchFieldException ex)
						{
							throw new PropertyError("Invalid discriminator constant %s.%s"
									.formatted(discriminator.toString(), constant.name()));
						}
					}).toList();
		}

		public static Class<?>
		extract(FieldAttribute discriminator, Enum<?> constant)
		{
			try
			{
				return discriminator.getRawType()
						.getField(constant.name())
						.getAnnotation(Subtype.class).value();
			} catch (NoSuchFieldException ex)
			{
				throw new PropertyError("Invalid discriminator constant %s.%s"
						.formatted(discriminator.toString(), constant.name()));
			}
		}
	}
}