package gate.lang.constructionStrategy;

import gate.annotation.Subtype;
import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.util.LinkedHashMap;
import java.util.Map;

public record DiscriminatorConstructorStrategy(Attribute discriminator) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		return construct(type, null, attributes);
	}

	@Override
	public Object construct(Class<?> type,
	                        Object value,
	                        Map<Attribute, Object> propertyMap,
	                        TriFunction<Attribute, Object, Object, Object> getValue)
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		for (var entry : propertyMap.entrySet())
			attributes.put(entry.getKey(), getValue.apply(entry.getKey(), null, entry.getValue()));

		return construct(type, value, attributes);
	}

	private Object construct(Class<?> type, Object value, Map<Attribute, Object> attributes)
	{
		try
		{
			if (!attributes.containsKey(discriminator)
					|| attributes.get(discriminator) == null)
				throw new ConstructionException(type, attributes.keySet());

			var discriminatorValue = attributes.get(discriminator);
			if (!(discriminatorValue instanceof Enum<?> constant)
					|| constant.getDeclaringClass() != discriminator.getRawType())
				throw new ConstructionException("Invalid discriminator value %s for %s.%s"
						.formatted(discriminatorValue, discriminator.getOwner().getName(), discriminator));

			var subtype = discriminator.getRawType()
					.getField(constant.name())
					.getAnnotation(Subtype.class).value();

			var target = subtype.isInstance(value) ? value : null;

			var remaining = new LinkedHashMap<Attribute, Object>();
			for (var entry : attributes.entrySet())
				if (!entry.getKey().equals(discriminator))
					remaining.put(entry.getKey(), entry.getValue());

			if (remaining.isEmpty())
				value = target;
			else if (target != null)
				value = ConstructionStrategy.newInstance(subtype, target, remaining,
						(attribute, currentValue, sourceValue) -> sourceValue);
			else
				value = ConstructionStrategy.newInstance(subtype, remaining);

			if (value != null && discriminator.getValue(value) == null)
				discriminator.setValue(value, discriminatorValue);

			return value;

		} catch (NoSuchFieldException ex)
		{
			throw new ConstructionException("Invalid discriminator value %s for %s.%s"
					.formatted(attributes.get(discriminator), discriminator.getOwner().getName(), discriminator));
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException("Could not update discriminated %s with attributes %s: %s"
					.formatted(type.getName(), attributes.keySet(), ex.getMessage()));
		}
	}
}
