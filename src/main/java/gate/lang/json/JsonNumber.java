package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import gate.type.Percentage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
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

	private static final DecimalFormat FORMAT
		= (DecimalFormat) DecimalFormat
			.getInstance(Locale.ENGLISH);

	private static final long serialVersionUID = 1L;

	static
	{
		FORMAT.setParseBigDecimal(true);
	}

	private JsonNumber(BigDecimal value)
	{
		this.value = value;
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

	public static JsonNumber valueOf(long value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber valueOf(double value)
	{
		return new JsonNumber(BigDecimal.valueOf(value));
	}

	public static JsonNumber valueOf(String string)
	{
		return new JsonNumber(new BigDecimal(string));
	}

	public static JsonNumber valueOf(Number number)
	{
		if (number instanceof Byte)
			return new JsonNumber(BigDecimal.valueOf((Byte) number));
		if (number instanceof Short)
			return new JsonNumber(BigDecimal.valueOf((Short) number));
		if (number instanceof Integer)
			return new JsonNumber(BigDecimal.valueOf((Integer) number));
		if (number instanceof Long)
			return new JsonNumber(BigDecimal.valueOf((Long) number));
		if (number instanceof Float)
			return new JsonNumber(BigDecimal.valueOf((Float) number));
		if (number instanceof Double)
			return new JsonNumber(BigDecimal.valueOf((Double) number));
		if (number instanceof BigDecimal)
			return new JsonNumber(((BigDecimal) number));
		if (number instanceof Percentage)
			return new JsonNumber(((Percentage) number).getValue());
		return valueOf(Objects.requireNonNull(number).toString());
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
		return FORMAT.format(this.value);
	}
}
