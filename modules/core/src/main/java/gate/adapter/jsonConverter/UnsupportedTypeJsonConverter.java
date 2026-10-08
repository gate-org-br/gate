package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;

import java.lang.reflect.Type;

public class UnsupportedTypeJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		throw new UnsupportedOperationException("Objects of type %s can't be serialized"
				.formatted(genericType.getTypeName()));
	}

	@Override
	public JsonElement toJson(Class<?> type, Object object)
	{
		throw new UnsupportedOperationException("Objects of type %s can't be serialized"
				.formatted(type.getName()));
	}
}