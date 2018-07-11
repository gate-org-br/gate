package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

/**
 * Represents a JSON number as a BigDecimal.
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonNumber extends BigDecimal implements JsonElement
{

	public JsonNumber(char[] in, int offset, int len)
	{
		super(in, offset, len);
	}

	public JsonNumber(char[] in, int offset, int len, MathContext mc)
	{
		super(in, offset, len, mc);
	}

	public JsonNumber(char[] in)
	{
		super(in);
	}

	public JsonNumber(char[] in, MathContext mc)
	{
		super(in, mc);
	}

	public JsonNumber(String val)
	{
		super(val);
	}

	public JsonNumber(String val, MathContext mc)
	{
		super(val, mc);
	}

	public JsonNumber(double val)
	{
		super(val);
	}

	public JsonNumber(double val, MathContext mc)
	{
		super(val, mc);
	}

	public JsonNumber(BigInteger val)
	{
		super(val);
	}

	public JsonNumber(BigInteger val, MathContext mc)
	{
		super(val, mc);
	}

	public JsonNumber(BigInteger unscaledVal, int scale)
	{
		super(unscaledVal, scale);
	}

	public JsonNumber(BigInteger unscaledVal, int scale, MathContext mc)
	{
		super(unscaledVal, scale, mc);
	}

	public JsonNumber(int val)
	{
		super(val);
	}

	public JsonNumber(int val, MathContext mc)
	{
		super(val, mc);
	}

	public JsonNumber(long val)
	{
		super(val);
	}

	public JsonNumber(long val, MathContext mc)
	{
		super(val, mc);
	}

	@Override
	public Type getType()
	{
		return Type.NUMBER;
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
	 * Formats the specified JsonNumber into a JSON formatted string.
	 *
	 * @param jsonNumber the JsonNumber object to be formatted on JSON notation
	 *
	 * @return a JSON formatted string representing the specified JsonNumber
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static String format(JsonNumber jsonNumber)
	{
		Objects.requireNonNull(jsonNumber);
		return JsonElement.format(jsonNumber);
	}
}
