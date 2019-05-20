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

	@Override
	public String toString()
	{
		return "null";
	}

	/**
	 * Parses a JSON formatted string into a JsonNull object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonNull object
	 *
	 * @return a JsonBoolean object representing the JSON formatted string specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse the specified JSON formatted string
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static JsonNull parse(String json) throws ConversionException
	{
		Objects.requireNonNull(json);

		JsonElement element = JsonElement.parse(json);
		if (element.getType() != JsonElement.Type.NULL)
			throw new ConversionException("the specified JsonElement is not a JsonNull");
		return (JsonNull) element;
	}
}
