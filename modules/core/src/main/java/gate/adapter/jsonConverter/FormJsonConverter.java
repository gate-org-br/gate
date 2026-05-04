package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.type.Form;

import java.lang.reflect.Type;

public class FormJsonConverter implements JsonConverter
{
	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		return element != null ? Form.valueOf(element) : null;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return object != null ? ((Form) object).toJson() : null;
	}
}
