package gate.lang.constructionStrategy;

import gate.annotation.Subtype;
import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.lang.property.FieldAttribute;

import java.util.Map;
import java.util.stream.Collectors;

public record DiscriminatorConstructorStrategy(FieldAttribute discriminator) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		if (!attributes.containsKey(discriminator))
			throw new ConstructionException("Missing discriminator attribute %s for %s"
					.formatted(discriminator, discriminator.getOwner().getName()));

		if (!(attributes.get(discriminator) instanceof Enum<?> constant)
				|| constant.getDeclaringClass() != discriminator.getRawType())
			throw new ConstructionException("Invalid discriminator value %s for %s.%s"
					.formatted(attributes.get(discriminator),
							discriminator.getOwner().getName(), discriminator));

		var subtype = Subtype.Extractor.extract(discriminator, constant);

		attributes = attributes.entrySet()
				.stream()
				.filter(entry -> !entry.getKey().equals(discriminator))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (attributes.isEmpty())
			return null;

		var value = ConstructionStrategy.newInstance(subtype, attributes);
		if (value != null && discriminator.getValue(value) == null)
			discriminator.setValue(value, constant);

		return value;
	}
}
