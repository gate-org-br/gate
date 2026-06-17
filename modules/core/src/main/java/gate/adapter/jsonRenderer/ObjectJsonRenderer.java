package gate.adapter.jsonRenderer;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.lang.property.FieldAttribute;

import java.util.*;

public class ObjectJsonRenderer implements JsonRenderer
{
	private final ThreadLocal<Deque<Object>> stack = ThreadLocal.withInitial(LinkedList::new);

	@Override
	public JsonElement renderJson(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		boolean pushed = false;
		Deque<Object> deque = stack.get();

		try
		{
			if (deque.stream().anyMatch(e -> e == object))
				return null;

			pushed = true;
			deque.push(object);

			JsonObject result = new JsonObject();

			var attributes = FieldAttribute.getAttributes(type);
			for (FieldAttribute attribute : attributes.values())
			{
				Object value = attribute.getValue(object);
				if (shouldWrite(value))
				{
					String name = Objects.requireNonNullElse(attribute.getMetadata().name(), attribute.toString());
					result.put(name, JsonRenderer.render(value));
				}
			}

			return result;
		} finally
		{
			if (pushed)
				deque.pop();
			if (deque.isEmpty())
				stack.remove();
		}
	}

	private boolean shouldWrite(Object value)
	{
		return value != null
				&& (!(value instanceof Collection<?> collection) || !collection.isEmpty())
				&& (!(value instanceof Map<?, ?> map) || !map.isEmpty())
				&& (!value.getClass().isArray() || java.lang.reflect.Array.getLength(value) > 0);
	}
}
