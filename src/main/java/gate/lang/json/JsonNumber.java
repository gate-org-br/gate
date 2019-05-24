package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import gate.type.Money;
import gate.type.Percentage;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a JSON number as a BigDecimal.
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonNumber extends Number implements JsonElement
{

	private final BigDecimal value;

	private static final long serialVersionUID = 1L;

	private JsonNumber(BigDecimal value)
	{
		this.value = Objects.requireNonNull(value);
	}

	@Override
	public Type getType()
	{
		return Type.NUMBER;
	}

	@Override
	public int intValue()
	{
		return value.intValue();
	}

	@Override
	public short shortValue()
	{
		return value.shortValue();
	}

	@Override
	public byte byteValue()
	{
		return value.byteValue();
	}

	@Override
	public long longValue()
	{
		return value.longValue();
	}

	@Override
	public double doubleValue()
	{
		return value.doubleValue();
	}

	@Override
	public float floatValue()
	{
		return value.floatValue();
	}

	/**
	 * Parses a JSON formatted string into a JsonNumber object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonNumber object
	 *
	 * @return a JsonNumber object representing the JSON formatted string specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse the specified JSON formatted string
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static JsonNumber parse(String json) throws ConversionException
	{
		Objects.requireNonNull(json);

		JsonElement element = JsonElement.parse(json);
		if (element.getType() != JsonElement.Type.NUMBER)
			throw new ConversionException("the specified JsonElement is not a JsonNumber");
		return (JsonNumber) element;
	}

	/**
	 * Returns an JsonNumber instance representing the specified int value.
	 *
	 * @param value an int value.
	 *
	 * @return a JsonNumber instance representing value.
	 */
	public static JsonNumber of(long value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified double value.
	 *
	 * @param value a double value.
	 *
	 * @return a JsonNumber instance representing value.
	 */
	public static JsonNumber of(double value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified string value.
	 *
	 * @param value a string value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(String value)
	{
		return new JsonNumber(new BigDecimal(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified Byte value.
	 *
	 * @param value a Byte value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Byte value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified Short value.
	 *
	 * @param value a Short value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Short value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified Integer value.
	 *
	 * @param value a Integer value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Integer value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified Long value.
	 *
	 * @param value a Long value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Long value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified Float value.
	 *
	 * @param value a Float value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Float value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified Double value.
	 *
	 * @param value a Double value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Double value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Returns an JsonNumber instance representing the specified BigDecimal value.
	 *
	 * @param value a BigDecimal value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(BigDecimal value)
	{
		return new JsonNumber(value);
	}

	/**
	 * Returns an JsonNumber instance representing the specified Percentage value.
	 *
	 * @param value a Percentage value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Percentage value)
	{
		return new JsonNumber(value.getValue());
	}

	/**
	 * Returns an JsonNumber instance representing the specified Money value.
	 *
	 * @param value a Money value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Money value)
	{
		return new JsonNumber(value.getValue());
	}

	/**
	 * Returns an JsonNumber instance representing the specified Number value.
	 *
	 * @param value a Number value.
	 *
	 * @return a JsonNumber instance representing value.
	 *
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber of(Number value)
	{
		return JsonNumber.of(value.toString());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof JsonNumber
			&& value.compareTo(((JsonNumber) obj).value) == 0;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String toString()
	{
		return value.toPlainString();
	}
}
