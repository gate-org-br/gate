package gate.annotation;

import gate.lang.property.FieldAttribute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	}
}