package gate.lang.json;

import gate.error.ConversionException;

import java.util.Objects;

/**
 * Represents a JSON null.
 *
 * @author Davi Nunes da Silva
 */
public class JsonNull implements JsonElement, JsonScalar
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
	 * Converts this JSON null to the specified Java type.
	 *
	 * @param <T>  the target Java type
	 * @param type the target Java type
	 * @return always {@code null}
	 */
	@Override
	public <T> T decode(Class<T> type) {return null;}

	/**
	 * Converts this JSON null to the specified parameterized Java type.
	 *
	 * @param <T>         the target Java type
	 * @param type        the target raw Java type
	 * @param elementType ignored for JSON null
	 * @return always {@code null}
	 */
	@Override
	public <T> T decode(java.lang.reflect.Type type, java.lang.reflect.Type elementType) {return null;}

	@Override
	public Object getScalarValue()
	{
		return null;
	}

	/**
	 * Returns the natural Java representation of JSON null.
	 *
	 * @return always {@code null}
	 */
	@Override public Object unwrap() {return null;}

	/**
	 * Parses a JSON formatted string into a JsonNull object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonNull object
	 * @return a JsonBoolean object representing the JSON formatted string specified
	 * @throws ConversionException  if an error occurs while trying to parse the specified JSON formatted string
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

	public static JsonNull render()
	{
		return INSTANCE;
	}

	public static JsonNull of()
	{
		return INSTANCE;
	}
}