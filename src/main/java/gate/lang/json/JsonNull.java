package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import java.util.Objects;

/**
 * Represents a JSON null.
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonNull implements JsonElement
{

	public static final JsonNull INSTANCE = new JsonNull();

	private JsonNull()
	{

	}

	@Override
	public Type getType()
	{
		return Type.NULL;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof JsonNull;
	}

	@Override
	public int hashCode()
	{
		return 0;
	}

	public static JsonNull parse(String string) throws ConversionException
	{
		return (JsonNull) JsonElement.parse(string);
	}

	public static String format(JsonNull jsonNull)
	{
		Objects.requireNonNull(jsonNull);
		return JsonElement.format(jsonNull);
	}
}
