package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;

import java.lang.reflect.Type;

public class StringJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element == null || element instanceof JsonNull ? null : element.decode(String.class);
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return JsonElement.wrap(object);
	}
}
