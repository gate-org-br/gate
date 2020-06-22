package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import java.util.Objects;

/**
 * Represents a JSON string.
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonString implements JsonElement
{

	private final String value;

	private JsonString(String value)
	{
		this.value = value;
	}

	@Override
	public Type getType()
	{
		return Type.STRING;
	}

	/**
	 * Gets the value of this JSON string.
	 *
	 * @return the value of this JSON string
	 */
	public String getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof JsonString
			&& ((JsonString) obj).value.equals(value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String toString()
	{
		return value;
	}

	/**
	 * Parses a JSON formatted string into a JsonString objecZt.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonString object
	 *
	 * @return a JsonString object representing the JSON formatted string specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse the specified JSON formatted string
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static JsonString parse(String json) throws ConversionException
	{
		Objects.requireNonNull(json);

		JsonElement element = JsonElement.parse(json);
		if (element.getType() != JsonElement.Type.STRING)
			throw new ConversionException("the specified JsonElement is not a JsonString");
		return (JsonString) element;
	}

	public static JsonString of(String string)
	{
		return new JsonString(string);
	}
}
