package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents any JSON element.
 *
 * @author davins
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public interface JsonElement extends Serializable
{

	/**
	 * Gets the type of this JSON element.
	 *
	 * @return the type of this JSON element
	 */
	Type getType();

	/**
	 * Type of a JSON element.
	 */
	enum Type
	{
		STRING, NUMBER, BOOLEAN, ARRAY, OBJECT, NULL
	}

	public static JsonElement parse(String string)
		throws ConversionException
	{
		try (JsonParser parser = new JsonParser(new StringReader(string)))
		{
			return parser.parse().get();
		}
	}

	/**
	 * Formats the specified JsonElement on JSON notation.
	 * <p>
	 * If the specified JsonElement is a JsonArray or a JsonObject, it's elements will be formatted recursively as their respective elements on JSON notation.
	 *
	 * @param element the JsonElement to be formatted on JSON notation
	 *
	 * @return the specified JsonElement formatted using JSON notation
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static String format(JsonElement element)
	{
		Objects.requireNonNull(element);
		try (StringWriter stringWriter = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(stringWriter);
			JsonFormatter jsonFormatter = new JsonFormatter(jsonWriter))
		{
			jsonFormatter.format(element);
			return stringWriter.toString();
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Creates a JsonElement for the specified object.
	 * <p>
	 * Boolean, Number, String, Collections, Array and null objects will be converted respectively to JsonBoolean, JsonNumber, JsonString, JsonArray, JsonArray and JsonNull objects.
	 * <p>
	 * Other object types will be formatted as JsonString objects using the associated Converter.toString method
	 *
	 * @param obj the object to be formatted
	 *
	 * @return a JsonElement representing the specified object
	 */
	public static JsonElement valueOf(Object obj)
	{
		if (obj == null)
			return JsonNull.INSTANCE;
		if (obj instanceof Boolean)
			return JsonBoolean.valueOf((Boolean) obj);
		if (obj instanceof Number)
			return JsonNumber.valueOf((Number) obj);
		if (obj instanceof String)
			return JsonString.valueOf((String) obj);

		if (obj instanceof Collection<?>)
			return JsonArray.valueOf((Collection<?>) obj);
		if (obj instanceof Object[])
			return JsonArray.valueOf((Object[]) obj);

		return JsonString.valueOf(gate.converter.Converter.toString(obj));
	}

	/**
	 * Creates a JsonElement for the specified object.
	 * <p>
	 * Boolean, Number, String, Collections, Array and null objects will be converted respectively to JsonBoolean, JsonNumber, JsonString, JsonArray, JsonArray and JsonNull objects.
	 * <p>
	 * Other object types will be formatted as JsonString objects using the associated Converter.toText method
	 *
	 * @param obj the object to be formatted
	 *
	 * @return a JsonElement representing the specified object
	 */
	public static JsonElement format(Object obj)
	{
		if (obj == null)
			return JsonNull.INSTANCE;
		if (obj instanceof Boolean)
			return JsonBoolean.valueOf((Boolean) obj);
		if (obj instanceof Number)
			return JsonNumber.valueOf((Number) obj);
		if (obj instanceof String)
			return JsonString.valueOf((String) obj);

		if (obj instanceof Collection<?>)
			return JsonArray.format((Collection<?>) obj);
		if (obj instanceof Object[])
			return JsonArray.format((Object[]) obj);

		return JsonString.valueOf(gate.converter.Converter.toText(obj));
	}
}
