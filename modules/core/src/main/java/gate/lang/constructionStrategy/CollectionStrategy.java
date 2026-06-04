package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;

import java.util.Map;
import java.util.function.Supplier;

record CollectionStrategy(Supplier<Object> supplier) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		var value = supplier.get();
		for (var attribute : attributes.entrySet())
			attribute.getKey().setValue(value, attribute.getValue());
		return value;
	}
}