package gate.adapter.jsonConverter;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.util.Reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class ArrayJsonConverter implements JsonConverter
{
	@Override
	public Object ofJson(Type genericType, JsonElement element)
	{
		if (!(element instanceof JsonArray jsonArray))
			return null;

		var elementType = Reflection.getElementGenericType(genericType);
		var result = Array.newInstance(Reflection.getRawType(elementType), jsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++)
			Array.set(result, i, JsonConverter.fromJson(elementType, jsonArray.get(i)));
		return result;
	}

	@Override
	public JsonElement toJson(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		JsonArray result = new JsonArray();
		int length = Array.getLength(object);
		for (int i = 0; i < length; i++)
		{
			Object value = Array.get(object, i);
			result.add(value != null ? JsonConverter.toJson(value) : JsonNull.INSTANCE);
		}
		return result;
	}
}
