package gate.adapter.renderer;

import gate.adapter.converter.Converter;
import gate.error.ConversionException;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.lang.property.FieldAttribute;

import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;

public class ObjectRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return Converter.toString(object);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format, render(type, object));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void toJsonText(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		int i = 0;
		var attributes = FieldAttribute.getAttributes(type);
		for (FieldAttribute attribute : attributes.values())
		{
			Object value = attribute.getFieldValue(object);
			if (shouldWrite(value) && stack.stream().noneMatch(e -> e == value))
			{
				if (i++ > 0)
					writer.write(JsonToken.Type.COMMA, null);

				String name = Objects.requireNonNullElse(attribute.getMetadata().name(), attribute.toString());

				writer.write(JsonToken.Type.STRING, name);
				writer.write(JsonToken.Type.DOUBLE_DOT, null);

				stack.push(value);
				attribute.getRenderer().toJsonText(stack, writer, (Class<Object>) attribute.getRawType(), value);
				stack.pop();
			}
		}

		writer.write(JsonToken.Type.CLOSE_OBJECT, null);
	}

	private boolean shouldWrite(Object value)
	{
		return value != null
		       && (!(value instanceof Collection<?> collection) || !collection.isEmpty())
		       && (!(value instanceof Map<?, ?> map) || !map.isEmpty())
		       && (!value.getClass().isArray() || java.lang.reflect.Array.getLength(value) > 0);
	}
}