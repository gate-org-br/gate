package gate.adapter.jsonRenderer;

import gate.adapter.renderer.Renderer;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

public class MonthJsonRenderer implements JsonRenderer
{
	@Override
	public JsonElement renderJson(Class<?> type, Object object)
	{
		return object != null ? JsonString.wrap(Renderer.render(object)) : null;
	}
}