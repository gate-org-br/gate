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
				throw new ConstructionException("Attribute '%s' does not belong to the hierarchy of sealed '%s'"
						.formatted(attribute, root.getName()));
			if (owner == root)
				continue;
			if (selected != null && selected != owner)
				throw new ConstructionException(
						"Ambiguous subtype resolution for sealed '%s': attributes point to both '%s' and '%s'"
								.formatted(root.getName(), selected.getName(), owner.getName()));
			selected = owner;
		}

		if (selected == null)
			throw new ConstructionException("""
					Cannot resolve subtype of sealed '%s': all provided attributes belong to the root
					type and none discriminates a single subtype"""
					.formatted(root.getName()));

		return ConstructionStrategy.newInstance(selected, attributes);
	}
}