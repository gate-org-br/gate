package gate.adapter.jsonConverter;

import gate.adapter.converter.Converter;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element instanceof JsonString jsonString ?
				Converter.fromString(LocalDate.class, jsonString.unwrap())
				: null;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return object != null
				? JsonString.wrap(Converter.toISOString(object)) : null;
	}
}