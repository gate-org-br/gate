package gate.adapter.jsonRenderer;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;

public class CollectionJsonRenderer implements JsonRenderer
{
	@Override
	public JsonElement renderJson(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		JsonArray result = new JsonArray();
		for (Object value : (Iterable<?>) object)
			result.add(value != null ? JsonRenderer.render(value) : JsonNull.INSTANCE);
		return result;
	}
}