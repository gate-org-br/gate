package gate.adapter.jsonConverter;

import gate.error.ConversionException;
import gate.lang.constructionStrategy.ConstructionStrategy;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.lang.property.Attribute;
import gate.lang.property.FieldAttribute;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ObjectJsonConverter implements JsonConverter
{
	ThreadLocal<Deque<Object>> stack = ThreadLocal.withInitial(LinkedList::new);

	@Override
	public Object ofJson(Type genericType,
	                     JsonElement element)
	{
		if (!(element instanceof JsonObject jsonObject))
			return null;

		Class<?> type = Reflection.getRawType(genericType);

		Map<Attribute, Object> attributes = new HashMap<>();
		for (var attribute : FieldAttribute.getAttributes(type).entrySet())
			if (jsonObject.containsKey(attribute.getKey()))
				attributes.put(attribute.getValue(),
						JsonConverter.fromJson(attribute.getValue().getGenericType(),
								jsonObject.get(attribute.getKey())));
		try
		{
			return ConstructionStrategy.newInstance(type, attributes);
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
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
				Object value = attribute.getFieldValue(object);
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