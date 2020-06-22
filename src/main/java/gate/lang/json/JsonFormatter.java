package gate.lang.json;

import gate.error.ConversionException;
import java.util.Map;

public class JsonFormatter implements AutoCloseable
{

	private final JsonWriter writer;

	public JsonFormatter(JsonWriter writer)
	{
		this.writer = writer;
	}

	public void format(JsonElement element) throws ConversionException
	{
		switch (element.getType())
		{
			case NUMBER:
				format((JsonNumber) element);
				break;
			case ARRAY:
				format((JsonArray) element);
				break;
			case BOOLEAN:
				format((JsonBoolean) element);
				break;
			case OBJECT:
				format((JsonObject) element);
				break;
			case STRING:
				format((JsonString) element);
				break;
			case NULL:
				format((JsonNull) element);
		}
	}

	public void format(JsonNumber value) throws ConversionException
	{
		writer.write(JsonToken.Type.NUMBER, value.toString());
	}

	public void format(JsonString value) throws ConversionException
	{
		writer.write(JsonToken.Type.STRING, value.toString());
	}

	public void format(JsonBoolean value) throws ConversionException
	{
		writer.write(value == JsonBoolean.TRUE ? JsonToken.Type.TRUE : JsonToken.Type.FALSE, null);
	}

	public void format(JsonNull value) throws ConversionException
	{
		writer.write(JsonToken.Type.NULL, null);
	}

	public void format(JsonArray value) throws ConversionException
	{
		boolean first = true;
		writer.write(JsonToken.Type.OPEN_ARRAY, null);
		for (JsonElement element : value)
		{
			if (!first)
				writer.write(JsonToken.Type.COMMA, null);

			format(element);

			first = false;
		}
		writer.write(JsonToken.Type.CLOSE_ARRAY, null);
	}

	public void format(JsonObject value) throws ConversionException
	{
		boolean first = true;
		writer.write(JsonToken.Type.OPEN_OBJECT, null);
		for (Map.Entry<String, JsonElement> entry : value.entrySet())
		{
			if (entry.getValue() != null)
			{
				if (first)
					first = false;
				else
					writer.write(JsonToken.Type.COMMA, null);

				writer.write(JsonToken.Type.STRING, entry.getKey());

				writer.write(JsonToken.Type.DOUBLE_DOT, null);

				format(entry.getValue());
			}
		}
		writer.write(JsonToken.Type.CLOSE_OBJECT, null);
	}

	@Override
	public void close()
	{
		writer.close();
	}
}
