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
	@Override public Boolean toObject() {return value;}

	@Override
	/**
	 * Converts this JSON boolean to the specified Java type.
	 *
	 * @param <T>  the target Java type
	 * @param type the target Java type
	 * @return the wrapped boolean converted to the requested type
	 */
	public <T> T toObject(Class<T> type)
	{
		return type.cast(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	/**
	 * Converts this JSON boolean to the specified parameterized Java type.
	 * <p>
	 * For scalar booleans, this behaves the same as {@link #toObject(Class)}.
	 *
	 * @param <T>         the target Java type
	 * @param type        the target raw Java type
	 * @param elementType ignored for scalar booleans
	 * @return the wrapped boolean converted to the requested type
	 */
	public <T> T toObject(java.lang.reflect.Type type, java.lang.reflect.Type elementType) {return (T) (Boolean) value;}

	public static JsonBoolean of(boolean value) {return value ? JsonBoolean.TRUE : JsonBoolean.FALSE;}

	public static JsonBoolean of(Boolean value) {return Boolean.TRUE.equals(value) ? JsonBoolean.TRUE : JsonBoolean.FALSE;}

	public static JsonBoolean format(boolean value) {return of(value);}

	public static JsonBoolean format(Boolean value)
	{
		return of(value);
	}

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
