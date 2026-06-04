package gate.lang.constructionStrategy;

import gate.lang.property.ArrayAttribute;
import gate.lang.property.ArrayElementsAttribute;
import gate.lang.property.Attribute;
import gate.util.Toolkit;

import java.lang.reflect.Array;
import java.util.Map;

record ArrayStrategy() implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		var elements = attributes.entrySet().stream()
				.filter(e -> e.getKey() instanceof ArrayElementsAttribute)
				.findFirst()
				.orElse(null);

		if (elements != null)
			return fromElements(type, elements.getValue());

		return fromIndexedValues(type, attributes);
	}

	private Object fromElements(Class<?> type, Object values)
	{
		var items = Toolkit.list(values);
		var array = Array.newInstance(type.getComponentType(), items.size());
		for (int i = 0; i < items.size(); i++)
			Array.set(array, i, items.get(i));
		return array;
	}

	private Object fromIndexedValues(Class<?> type, Map<Attribute, Object> attributes)
	{
		int size = attributes.keySet().stream()
				.filter(ArrayAttribute.class::isInstance)
				.map(ArrayAttribute.class::cast)
				.mapToInt(ArrayAttribute::getIndex)
				.max()
				.orElse(-1) + 1;

		var array = Array.newInstance(type.getComponentType(), size);
		for (var entry : attributes.entrySet())
			if (entry.getKey() instanceof ArrayAttribute attribute)
				Array.set(array, attribute.getIndex(), entry.getValue());
		return array;
	}
}
