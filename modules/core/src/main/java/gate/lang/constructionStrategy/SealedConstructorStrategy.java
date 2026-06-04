package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selects an immediate subtype of a sealed root from the owner of the non-null attributes.
 * <p>
 * This strategy does not resolve constructors. After the subtype is selected, construction is
 * delegated to the regular {@link ConstructionStrategy} resolution of that subtype.
 */
public record SealedConstructorStrategy(Class<?> root, Map<Attribute, Class<?>> owners)
		implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		attributes = attributes.entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (attributes.isEmpty())
			return null;

		Class<?> selected = null;
		for (var attribute : attributes.keySet())
		{
			var owner = owners.get(attribute);
			if (owner == null)
				throw new ConstructionException(type, attributes.keySet());
			if (owner == root)
				continue;
			if (selected != null && selected != owner)
				throw new ConstructionException(type, attributes.keySet());
			selected = owner;
		}

		if (selected == null)
			throw new ConstructionException(type, attributes.keySet());

		return ConstructionStrategy.newInstance(selected, attributes);
	}
}