package gate.adapter.jsonConverter;

import gate.adapter.converter.Converter;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.lang.property.FieldAttribute;
import gate.lang.property.PropertyGraph;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class ObjectJsonConverter implements JsonConverter
{
	ThreadLocal<Deque<Object>> stack = ThreadLocal.withInitial(LinkedList::new);

	@Override
	public Object ofJson(Type genericType,
	                     JsonElement element)
	{
		if (element instanceof JsonString string)
			return Converter.fromString(genericType, string.unwrap());
		if (!(element instanceof JsonObject jsonObject))
			return null;

		Class<?> type = Reflection.getRawType(genericType);
		return PropertyGraph.of(type, new ArrayList<>(jsonObject.keySet()))
				.populate(e -> JsonConverter.fromJson(e.getGenericType(),
						jsonObject.get(e.getLastAttribute().toString())));
	}

	@Override
	public JsonElement toJson(Class<?> type, Object object)
	{
		if (object == null)
			return null;

		boolean pushed = false;
		Deque<Object> deque = stack.get();

		try
		{
			if (deque.stream().anyMatch(e -> e == object))
				return null;

			pushed = true;
			deque.push(object);

			JsonObject result = new JsonObject();

			var attributes = FieldAttribute.getAttributes(object.getClass());
			for (FieldAttribute attribute : attributes.values())
			{
				Object value = attribute.getValue(object);
				if (value != null)
					result.put(attribute.toString(), JsonConverter.toJson(value));
			}

			return result;
		} finally
		{
			if (pushed)
				deque.pop();
			if (deque.isEmpty())
				stack.remove();
		}
	}
}
