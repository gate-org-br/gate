package gate.lang.constructionStrategy;

import gate.adapter.collector.Collector;
import gate.lang.property.Attribute;
import gate.lang.property.CollectionAttribute;
import gate.util.Toolkit;

import java.util.Map;

record CollectionStrategy() implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		var elements = attributes.entrySet().stream()
				.filter(e -> e.getKey() instanceof CollectionAttribute)
				.findFirst()
				.orElse(null);

		return Collector.fromArray(type, elements != null
				? Toolkit.collection(elements.getValue()).toArray()
				: new Object[0]);
	}
}
