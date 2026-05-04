package gate.adapter.jsonConverter;

import gate.adapter.converter.Converter;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;
import gate.util.Reflection;

import java.lang.reflect.Type;

public class NumberJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element instanceof JsonNumber jsonNumber ? jsonNumber.decode(Reflection.getRawType(genericType)) : null;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return object != null ? JsonNumber.of(Converter.toISOString(object)) : null;
	}
}
