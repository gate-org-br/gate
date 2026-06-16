package gate.adapter.jsonConverter;

import gate.util.Instance;
import gate.adapter.converter.Converter;
import gate.adapter.registry.JsonConverterRegistry;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonObject;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class MapJsonConverter implements JsonConverter
{
	ThreadLocal<Deque<Object>> stack = ThreadLocal.withInitial(LinkedList::new);

	@SuppressWarnings({"rawtypes", "unchecked"}) @Override
	public Object ofJson(Type genericType, JsonElement element)
	{
		if (!(element instanceof JsonObject jsonObject))
			return null;

		var rawType = Reflection.getRawType(genericType);
		var keyType = Reflection.getKeyType(genericType);
		var elementType = Reflection.getValueGenericType(genericType);
		var result = (Map) Instance.create(rawType);
		for (var item : jsonObject.entrySet())
		{
			var key = Converter.getConverter(keyType).ofString(keyType, item.getKey());
			var value = JsonConverter.fromJson(elementType, item.getValue());
			result.put(key, value);
		}
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

			JsonObject result = new JsonObject();

			for (var entry : ((Map<?, ?>) object).entrySet())
			{
				if (entry.getKey() == null)
					continue;
				result.put(Converter.toString(entry.getKey()),
						entry.getValue() != null
								? JsonConverterRegistry.INSTANCE.get(entry.getValue().getClass()).toJson(entry.getValue().getClass(), entry.getValue())
								: JsonNull.INSTANCE);
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
}