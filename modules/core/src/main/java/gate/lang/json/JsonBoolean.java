package gate.lang.json;

import gate.error.ConversionException;

import java.util.Objects;

/**
 * Represents a JSON boolean.
 *
 * @author Davi Nunes da Silva
 */
public class JsonBoolean implements JsonElement, JsonScalar
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
	public Object getScalarValue()
	{
		return value;
	}

	@Override
	public Type getType()
	{
		return Type.BOOLEAN;
	}

	@Override
	public int hashCode() {return value ? 1 : 0;}

	@Override
	public String toString()
	{
		return Boolean.toString(value);
	}

	/**
	 * Returns the natural Java representation of this JSON boolean.
	 *
	 * @return the wrapped {@link Boolean} value
	 */
	@Override
	public Boolean unwrap() {return value;}


	/**
	 * Converts this JSON boolean to the specified Java type.
	 *
	 * @param <T>  the target Java type
	 * @param type the target Java type
	 * @return the wrapped boolean converted to the requested type
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T decode(Class<T> type)
	{
		return type == boolean.class ? (T) Boolean.valueOf(value) : type.cast(value);
	}

	public static JsonBoolean wrap(boolean value) {return value ? JsonBoolean.TRUE : JsonBoolean.FALSE;}

	public static JsonBoolean wrap(Boolean value) {return Boolean.TRUE.equals(value) ? JsonBoolean.TRUE : JsonBoolean.FALSE;}

	public static JsonBoolean render(boolean value) {return wrap(value);}

	public static JsonBoolean render(Boolean value) {return wrap(value);}

	/**
	 * Parses a JSON formatted string into a JsonBoolean object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonBoolean object
	 * @return a JsonBoolean object representing the JSON formatted string specified
	 * @throws ConversionException  if an error occurs while trying to parse the
	 *                              specified JSON formatted string
	 * @throws NullPointerException if any parse the parameters is null
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