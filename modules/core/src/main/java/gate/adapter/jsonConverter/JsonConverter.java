package gate.adapter.jsonConverter;

import gate.adapter.registry.JsonConverterRegistry;
import gate.lang.json.JsonElement;
import gate.util.Reflection;

import java.lang.reflect.Type;

public interface JsonConverter
{
	JsonElement toJson(Class<?> type, Object object);

	Object ofJson(Type genericType, JsonElement element);

	static JsonElement toJson(Object object)
	{
		return object != null
				? JsonConverterRegistry.INSTANCE.get(object.getClass())
				  .toJson(object.getClass(), object) : null;
	}

	@SuppressWarnings("unchecked")
	static <T> T fromJson(Class<?> type, JsonElement element)
	{
		return (T) JsonConverterRegistry.INSTANCE.get(Reflection.getRawType(type))
				.ofJson(type, element);
	}

	static Object fromJson(Type genericType, JsonElement element)
	{
		return JsonConverterRegistry.INSTANCE.get(Reflection.getRawType(genericType))
				.ofJson(genericType, element);
	}
}