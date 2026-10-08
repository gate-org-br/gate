package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;

import java.lang.reflect.Type;

public class JsonElementJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return (JsonElement) object;
	}
}
