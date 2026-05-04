package gate.adapter.jsonRenderer;

import gate.adapter.renderer.Renderer;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonObject;

import java.util.Map;

public class MapJsonRenderer implements JsonRenderer
{
	@Override public JsonElement render(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		JsonObject result = new JsonObject();
		for (var entry : ((Map<?, ?>) object).entrySet())
		{
			if (entry.getKey() == null)
				continue;
			result.put(Renderer.render(entry.getKey()),
					entry.getValue() != null ? JsonRenderer.render(entry.getValue()) : JsonNull.INSTANCE);
		}
		return result;
	}
}