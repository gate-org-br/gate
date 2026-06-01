package gate.adapter.jsonRenderer;

import gate.adapter.renderer.Renderer;
import gate.lang.json.JsonElement;

public class DefaultJsonRenderer implements JsonRenderer
{
	@Override
	public JsonElement renderJson(Class<?> type, Object object)
	{
		return object != null
				? JsonElement.wrap(Renderer.render(object)) : null;
	}
}