package gate.adapter.jsonConverter;

import gate.util.Instance;
import gate.adapter.registry.JsonConverterRegistry;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

public class CollectionJsonConverter implements JsonConverter
{
	ThreadLocal<Deque<Object>> stack = ThreadLocal.withInitial(LinkedList::new);

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		if (!(element instanceof JsonArray jsonArray))
			return null;

		var rawType = Reflection.getRawType(genericType);
		var elementType = Reflection.getElementGenericType(genericType);
		var result = (Collection) Instance.create(rawType);
		for (var item : jsonArray)
			result.add(JsonConverter.fromJson(elementType, item));
		return result;
	}

	@Override
	public JsonElement toJson(Class<?> type, Object object)
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

			JsonArray result = new JsonArray();

			for (var value : (Collection<?>) object)
				result.add(value != null
						? JsonConverterRegistry.INSTANCE.get(value.getClass()).toJson(value.getClass(), value)
						: JsonNull.INSTANCE);
			return result;
		} finally
		{
			if (pushed)
				deque.pop();
			if (deque.isEmpty())
				stack.remove();
		}
	}
}