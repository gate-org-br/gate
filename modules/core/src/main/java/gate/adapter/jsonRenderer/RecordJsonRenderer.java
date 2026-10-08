package gate.adapter.jsonRenderer;

import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecordJsonRenderer implements JsonRenderer
{
	private static final Map<Class<?>, RecordComponent[]> COMPONENTS = new ConcurrentHashMap<>();

	@Override
	public JsonElement renderJson(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		try
		{
			JsonObject result = new JsonObject();
			var components = COMPONENTS.computeIfAbsent(type, Class::getRecordComponents);

			for (var recordComponent : components)
			{
				Object value = recordComponent.getAccessor().invoke(object);
				if (value != null)
					result.put(recordComponent.getName(), JsonRenderer.render(value));
			}

			return result;
		} catch (IllegalAccessException | InvocationTargetException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}
}