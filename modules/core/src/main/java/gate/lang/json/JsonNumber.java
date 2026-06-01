package gate.lang.json;

import gate.error.ConversionException;
import gate.type.Money;
import gate.type.Percentage;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a JSON number as a BigDecimal.
 *
 * @author Davi Nunes da Silva
 */
public class JsonNumber extends Number implements JsonElement, JsonScalar
{

	private final BigDecimal value;

	@Serial
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

	public BigDecimal getValue()
	{
		return value;
	}


	/**
	 * Converts this JSON number to the specified Java numeric type.
	 *
	 * @param <T>  the target Java type
	 * @param type the target Java type
	 * @return the numeric value converted to the requested type
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(Class<T> type)
	{
		if (type == short.class || type == Short.class)
			return (T) Short.valueOf(value.shortValue());
		if (type == int.class || type == Integer.class)
			return (T) Integer.valueOf(value.intValue());
		if (type == long.class || type == Long.class)
			return (T) Long.valueOf(value.longValue());
		if (type == float.class || type == Float.class)
			return (T) Float.valueOf(value.floatValue());
		if (type == double.class || type == Double.class)
			return (T) Double.valueOf(value.doubleValue());
		if (type == byte.class || type == Byte.class)
			return (T) Byte.valueOf(value.byteValue());
		if (type.isAssignableFrom(BigInteger.class))
			return type.cast(value.toBigInteger());
		return type.cast(value);
	}

	/**
	 * Returns the natural Java representation of this JSON number.
	 *
	 * @return the wrapped {@link BigDecimal} value
	 */
	@Override
	public BigDecimal unwrap() {return value;}

	@Override
	public Object getScalarValue()
	{
		return value;
	}

	/**
	 * Parses a JSON-formatted string into a JsonNumber object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonNumber object
	 * @return a JsonNumber object representing the JSON formatted string specified
	 * @throws ConversionException  if an error occurs while trying to parse the
	 *                              specified JSON formatted string
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
	 * Returns a JsonNumber instance representing the specified int value.
	 *
	 * @param value an int value.
	 * @return a JsonNumber instance representing value.
	 */
	public static JsonNumber wrap(long value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(long value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified double value.
	 *
	 * @param value a double value.
	 * @return a JsonNumber instance representing value.
	 */
	public static JsonNumber wrap(double value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(double value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified string value.
	 *
	 * @param value a string value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(String value)
	{
		return new JsonNumber(new BigDecimal(value));
	}

	public static JsonNumber render(String value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Byte value.
	 *
	 * @param value a Byte value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Byte value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(Byte value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Short value.
	 *
	 * @param value a Short value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Short value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(Short value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Integer value.
	 *
	 * @param value an Integer value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Integer value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(Integer value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Long value.
	 *
	 * @param value a Long value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Long value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(Long value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Float value.
	 *
	 * @param value a Float value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Float value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(Float value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Double value.
	 *
	 * @param value a Double value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Double value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber render(Double value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified BigDecimal value.
	 *
	 * @param value a BigDecimal value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(BigDecimal value)
	{
		return new JsonNumber(value);
	}

	public static JsonNumber render(BigDecimal value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Percentage value.
	 *
	 * @param value a Percentage value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Percentage value)
	{
		return new JsonNumber(value.getValue());
	}

	public static JsonNumber render(Percentage value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Money value.
	 *
	 * @param value money value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Money value)
	{
		return new JsonNumber(value.getValue());
	}

	public static JsonNumber render(Money value)
	{
		return wrap(value);
	}

	/**
	 * Returns a JsonNumber instance representing the specified Number value.
	 *
	 * @param value a Number value.
	 * @return a JsonNumber instance representing value.
	 * @throws NullPointerException if the specified value is null
	 */
	public static JsonNumber wrap(Number value)
	{
		return JsonNumber.wrap(value.toString());
	}

	public static JsonNumber render(Number value) {return wrap(value);}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof JsonNumber && value.compareTo(((JsonNumber) obj).value) == 0;
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