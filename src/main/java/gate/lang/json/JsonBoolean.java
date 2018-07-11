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

	public static JsonBoolean valueOf(boolean value)
	{
		return value ? JsonBoolean.TRUE : JsonBoolean.FALSE;
	}

	public static JsonBoolean valueOf(Boolean value)
	{
		return Boolean.TRUE.equals(value)
			? JsonBoolean.TRUE
			: JsonBoolean.FALSE;
	}

	public static JsonBoolean parse(String string) throws ConversionException
	{
		return (JsonBoolean) JsonElement.parse(string);
	}

	public static String format(JsonBoolean jsonBoolean)
	{
		Objects.requireNonNull(jsonBoolean);
		return JsonElement.format(jsonBoolean);
	}
}
