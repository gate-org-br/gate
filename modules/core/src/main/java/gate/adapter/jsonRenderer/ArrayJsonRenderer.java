package gate.adapter.jsonRenderer;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;

import java.lang.reflect.Array;

public class ArrayJsonRenderer implements JsonRenderer
{
	@Override public JsonElement render(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		JsonArray result = new JsonArray();
		int length = Array.getLength(object);
		for (int i = 0; i < length; i++)
		{
			Object value = Array.get(object, i);
			result.add(value != null ? JsonRenderer.render(value) : JsonNull.INSTANCE);
		}
		return result;
	}
}