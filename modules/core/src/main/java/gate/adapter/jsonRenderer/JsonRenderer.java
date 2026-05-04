package gate.adapter.jsonRenderer;

import gate.adapter.registry.JsonRendererRegistry;
import gate.lang.json.JsonElement;

public interface JsonRenderer
{
	JsonElement render(Class<?> type, Object object);

	static JsonElement render(Object object)
	{
		if (object == null)
			return null;

		return JsonRendererRegistry.INSTANCE.get(object.getClass())
				.render(object.getClass(), object);

	}
}