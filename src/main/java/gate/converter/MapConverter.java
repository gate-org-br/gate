package gate.converter;

import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.util.Reflection;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapConverter extends ObjectConverter
{

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType)
			throws ConversionException
	{
		if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_OBJECT)
			throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

		boolean empty = true;

		Map object = new HashMap<>();

		do
		{
			scanner.scan();
			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
			{
				empty = false;
				if (scanner.getCurrent().getType() != JsonToken.Type.STRING)
					throw new ConversionException(
							scanner.getCurrent() + " is not a valid JSON object key");

				String key = scanner.getCurrent().toString();

				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.DOUBLE_DOT)
					throw new ConversionException(
							scanner.getCurrent() + " is not a valid JSON object");

				scanner.scan();
				Converter converter = Converter.getConverter((Class) elementType);
				Object value = converter.ofJson(scanner, elementType,
						Reflection.getElementType(elementType));
				object.put(key, value);
			} else if (!empty)
				throw new ConversionException("the specified JsonElement is not a JsonObject");
		} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
			throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

		scanner.scan();
		return object;
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		Map<?, ?> map = (Map<?, ?>) object;
		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		boolean first = true;
		for (Map.Entry entry : map.entrySet())
		{
			if (entry.getValue() != null)
			{
				if (first)
					first = false;
				else
					writer.write(JsonToken.Type.COMMA, null);

				writer.write(JsonToken.Type.STRING, Converter.toString(entry.getKey()));
				writer.write(JsonToken.Type.DOUBLE_DOT, null);
				Converter converter = Converter.getConverter(entry.getValue().getClass());
				converter.toJson(writer, (Class<Object>) entry.getValue().getClass(),
						entry.getValue());
			}
		}

		writer.write(JsonToken.Type.CLOSE_OBJECT, null);
	}

	@Override
	public <T> void toJsonText(JsonWriter writer, Class<T> type, T object)
			throws ConversionException
	{
		Map<?, ?> map = (Map<?, ?>) object;
		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		boolean first = true;
		for (Map.Entry entry : map.entrySet())
		{
			if (entry.getValue() != null)
			{
				if (first)
					first = false;
				else
					writer.write(JsonToken.Type.COMMA, null);

				writer.write(JsonToken.Type.STRING, Converter.toString(entry.getKey()));
				writer.write(JsonToken.Type.DOUBLE_DOT, null);
				Converter converter = Converter.getConverter(entry.getValue().getClass());
				converter.toJsonText(writer, (Class<Object>) entry.getValue().getClass(),
						entry.getValue());
			}
		}

		writer.write(JsonToken.Type.CLOSE_OBJECT, null);
	}
}
