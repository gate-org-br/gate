package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import java.util.Objects;

/**
 * Represents a JSON boolean.
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonBoolean implements JsonElement
{

	private final boolean value;

	/**
	 * Represents a JSON true boolean.
	 */
	public static final JsonBoolean TRUE = new JsonBoolean(true);

	/**
	 * Represents a JSON false boolean.
	 */
	public static final JsonBoolean FALSE = new JsonBoolean(false);

	private JsonBoolean(boolean value)
	{
		this.value = value;
	}

	/**
	 * Gets the boolean value associated with this JSON boolean.
	 *
	 * @return the boolean value associated with this JSON boolean
	 */
	public boolean getValue()
	{
		return value;
	}

	@Override
	public Type getType()
	{
		return Type.BOOLEAN;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof JsonBoolean
			&& obj == this;
	}

	@Override
	public int hashCode()
	{
		return value ? 1 : 0;
	}

	@Override
	public String toString()
	{
		return value ? "true" : "false";
	}

	public static JsonBoolean of(boolean value)
	{
		return value ? JsonBoolean.TRUE : JsonBoolean.FALSE;
	}

	public static JsonBoolean of(Boolean value)
	{
		return Boolean.TRUE.equals(value)
			? JsonBoolean.TRUE
			: JsonBoolean.FALSE;
	}

	/**
	 * Parses a JSON formatted string into a JsonBoolean object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonBoolean object
	 *
	 * @return a JsonBoolean object representing the JSON formatted string specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse the specified JSON formatted string
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static JsonBoolean parse(String json) throws ConversionException
	{
		Objects.requireNonNull(json);

		JsonElement element = JsonElement.parse(json);
		if (element.getType() != JsonElement.Type.BOOLEAN)
			throw new ConversionException("the specified JsonElement is not a JsonBoolean");
		return (JsonBoolean) element;
	}
}
