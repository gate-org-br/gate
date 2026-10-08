package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element instanceof JsonNumber number ? Duration.ofSeconds(number.longValue()) : null;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return object instanceof Duration duration
				? JsonNumber.wrap(duration.getSeconds()) : null;
	}
}