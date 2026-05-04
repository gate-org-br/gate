package gate.adapter.jsonConverter;

import gate.adapter.converter.Converter;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class PatternJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element instanceof JsonString jsonString
				? Converter.fromString(Pattern.class, jsonString.unwrap())
				: null;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return object != null
				? JsonString.of(Converter.toString(object)) : null;
	}
}
