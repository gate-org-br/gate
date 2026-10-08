package gate.annotation;

import gate.lang.property.FieldAttribute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD,
		java.lang.annotation.ElementType.TYPE})
public @interface Default
{
	class Extractor
	{
		private Extractor() {}

		public static Class<?> extract(FieldAttribute discriminator)
		{
			return Subtype.Extractor
					.extract(discriminator)
					.stream().filter(e -> e.isAnnotationPresent(Default.class))
					.findAny()
					.orElse(null);
		}


		public static Enum<?> extract(Class<?> type)
		{
			if (!type.isEnum())
				return null;
			return Stream.of(type.getEnumConstants()).map(Enum.class::cast).filter(e ->
					{
						try
						{
							return type.getField(e.name())
									.isAnnotationPresent(Default.class);
						} catch (NoSuchFieldException ex)
						{
							throw new IllegalStateException(ex);
						}
					})
					.findAny()
					.orElse(null);
		}
	}
}