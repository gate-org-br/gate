package gate.adapter.renderer;

import gate.error.ConversionException;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;

import java.util.Deque;

public class CollectionRenderer extends ObjectRenderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		StringBuilder string = new StringBuilder();
		for (Object obj : ((Iterable<?>) object))
		{
			if (obj != null)
			{
				if (!string.isEmpty())
					string.append(", ");

				string.append(Renderer.render(obj));
			}
		}
		return string.toString();
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> void toJsonText(Deque<Object> stack, JsonWriter writer, Class<T> type, T object)
			throws ConversionException
	{
		writer.write(JsonToken.Type.OPEN_ARRAY, null);

		boolean first = true;
		for (Object element : ((Iterable<?>) object))
		{
			if (first)
				first = false;
			else
				writer.write(JsonToken.Type.COMMA, null);

			if (element != null)
			{
				Renderer renderer = Renderer.getRenderer(element.getClass());
				renderer.toJsonText(stack, writer, (Class<Object>) element.getClass(), element);
			} else
				writer.write(JsonToken.Type.NULL, null);
		}

		writer.write(JsonToken.Type.CLOSE_ARRAY, null);
	}
}