package gate.lang.constructionStrategy;

import gate.annotation.Subtype;
import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.FieldAttribute;

import java.util.HashMap;
import java.util.Map;

public record DiscriminatorConstructorStrategy(FieldAttribute discriminator,
                                               Class<?> defaultSubtype) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		if (!attributes.containsKey(discriminator))
			if (defaultSubtype != null)
				return ConstructionStrategy.newInstance(defaultSubtype, attributes);
			else
				throw new ConstructionException("Missing discriminator attribute '%s' for '%s'"
						.formatted(discriminator, discriminator.getOwner().getName()));

		var discriminatorValue = attributes.get(discriminator);
		if (!(discriminatorValue instanceof Enum<?> constant)
				|| constant.getDeclaringClass() != discriminator.getRawType())
			throw new ConstructionException(
					"Invalid discriminator value '%s' for '%s.%s': expected an enum constant of type '%s'"
							.formatted(discriminatorValue,
									discriminator.getOwner().getName(),
									discriminator,
									discriminator.getRawType().getName()));

		var subtype = Subtype.Extractor.extract(discriminator, constant);
		attributes = attributes.entrySet()
				.stream()
				.filter(entry -> !entry.getKey().equals(discriminator))
				.filter(e -> e.getValue() != null)
				.collect(HashMap::new, (m, e)
						-> m.put(e.getKey(), e.getValue()), HashMap::putAll);
		if (attributes.isEmpty())
			return null;
		var value = ConstructionStrategy.newInstance(subtype, attributes);
		if (value != null && discriminator.getValue(value) == null)
			discriminator.setValue(value, constant);
		return value;
	}
}