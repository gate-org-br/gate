package gate.lang.constructionStrategy;

import gate.adapter.collector.Collector;
import gate.lang.property.Attribute;
import gate.lang.property.ListAttribute;

import java.util.Map;

record IndexedCollectionStrategy() implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		int size = attributes.keySet().stream()
				.filter(ListAttribute.class::isInstance)
				.map(ListAttribute.class::cast)
				.mapToInt(ListAttribute::getIndex)
				.max()
				.orElse(-1) + 1;

		Object[] array = new Object[size];
		for (var entry : attributes.entrySet())
			if (entry.getKey() instanceof ListAttribute attribute)
				array[attribute.getIndex()] = entry.getValue();

		return Collector.fromArray(type, array);
	}
}
