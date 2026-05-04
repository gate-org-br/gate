package gate.lang.json;

import gate.adapter.converter.Converter;
import gate.error.ConversionException;

import java.util.Objects;

/**
 * Represents a JSON string.
 *
 * @author Davi Nunes da Silva
 */
public class JsonString implements JsonElement, JsonScalar
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
		return obj instanceof JsonString && ((JsonString) obj).value.equals(value);
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String toString()
	{
		return JsonElement.stringify(this);
	}

	@Override
	public Object getScalarValue()
	{
		return value;
	}

	@Override
	/**
	 * Converts this JSON string to the specified Java type.
	 * <p>
	 * When the target type is {@link String}, the wrapped value is returned
	 * directly. Otherwise the string is converted using the Gate converter
	 * infrastructure.
	 *
	 * @param <T>  the target Java type
	 * @param type the target Java type
	 * @return the converted Java value
	 */
	public <T> T decode(Class<T> type)
	{
		return type == String.class ? type.cast(value) : Converter.fromString(type, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	/**
	 * Converts this JSON string to the specified parameterized Java type.
	 * <p>
	 * For scalar strings, this behaves the same as {@link #decode(Class)}.
	 *
	 * @param <T>         the target Java type
	 * @param type        the target raw Java type
	 * @param elementType ignored for scalar strings
	 * @return the converted Java value
	 */
	public <T> T decode(java.lang.reflect.Type type, java.lang.reflect.Type elementType)
	{
		return decode((Class<T>) type);
	}

	/**
	 * Returns the natural Java representation of this JSON string.
	 *
	 * @return the wrapped {@link String} value
	 */
	@Override public String unwrap() {return value;}

	/**
	 * Parses a JSON formatted string into a JsonString objecZt.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonString object
	 * @return a JsonString object representing the JSON formatted string specified
	 * @throws ConversionException  if an error occurs while trying to parse the
	 *                              specified JSON formatted string
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

	public static JsonString render(String string)
	{
		return of(string);
	}
}
