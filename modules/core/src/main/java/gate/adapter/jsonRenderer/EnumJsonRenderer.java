package gate.adapter.jsonRenderer;

import gate.adapter.renderer.Renderer;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

public class EnumJsonRenderer implements JsonRenderer
{
	@Override public JsonElement render(Class<?> type, Object object)
	{
		return object != null ? JsonString.of(Renderer.render(object)) : null;
	}
}
