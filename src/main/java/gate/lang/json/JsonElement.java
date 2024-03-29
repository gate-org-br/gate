package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.error.UncheckedConversionException;
import gate.handler.JsonElementHandler;
import gate.util.Reflection;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
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

	public static final JsonString UNDEFINED = JsonString.of("Indefinido");

	/**
	 * Gets the type parse this JSON element.
	 *
	 * @return the type parse this JSON element
	 */
	Type getType();

	/**
	 * Type parse a JSON element.
	 */
	enum Type
	{
		NULL, STRING, NUMBER, BOOLEAN, ARRAY, OBJECT;

		/**
		 * return the JSON type associated with the specified object.
		 *
		 * @param obj the object whose JSON type is to be returned
		 * @return the JSON type associated with the specified object
		 */
		public static Type of(Object obj)
		{
			if (obj instanceof Type)
				return (Type) obj;
			if (obj instanceof JsonElement)
				return ((JsonElement) obj).getType();

			if (obj == null)
				return NULL;
			if (obj instanceof String)
				return STRING;
			if (obj instanceof Number)
				return NUMBER;
			if (obj instanceof Boolean)
				return BOOLEAN;
			if (obj instanceof Collection<?>)
				return ARRAY;
			if (obj instanceof Object[])
				return ARRAY;
			if (obj instanceof Map<?, ?>)
				return OBJECT;
			return STRING;
		}
	}

	static JsonElement parse(String string)
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
	 * If the specified JsonElement is a JsonArray or a JsonObject, it's elements will be formatted recursively as their respective elements on JSON
	 * notation.
	 *
	 * @param element the JsonElement to be formatted on JSON notation
	 *
	 * @return the specified JsonElement formatted using JSON notation
	 *
	 * @throws NullPointerException if any parse the parameters is null
	 */
	static String format(JsonElement element)
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

	public <T> T toObject(Class<T> type);

	public <T, E> T toObject(java.lang.reflect.Type type,
		java.lang.reflect.Type elementType);

	/**
	 * Creates a JsonElement for the specified object.
	 * <p>
	 * Boolean, Number, String, Collections, Array and null objects will be converted respectively to JsonBoolean, JsonNumber, JsonString, JsonArray,
	 * JsonArray and JsonNull objects.
	 * <p>
	 * Other object types will be converted as JsonObjects
	 *
	 * @param obj the object to be formatted
	 *
	 * @return a JsonElement representing the specified object
	 */
	static JsonElement of(Object obj)
	{
		if (obj == null)
			return JsonNull.INSTANCE;
		if (obj instanceof Boolean)
			return JsonBoolean.parse((Boolean) obj);
		if (obj instanceof Number)
			return JsonNumber.of((Number) obj);
		if (obj instanceof String)
			return JsonString.of((String) obj);

		if (obj instanceof Collection<?>)
			return JsonArray.of((Collection<?>) obj);
		if (obj instanceof Object[])
			return JsonArray.of((Object[]) obj);

		for (Constructor<?> constructor
			: obj.getClass().getDeclaredConstructors())
		{
			if (constructor.getParameterCount() == 0)
			{
				JsonObject result = new JsonObject();
				for (Field field : Reflection.getFields(Reflection.getRawType(obj.getClass())))
				{
					if (!Modifier.isStatic(field.getModifiers()))
					{
						try
						{
							field.setAccessible(true);
							Object value = field.get(obj);
							if (value != null)
								result.put(field.getName(), JsonElement.of(value));
						} catch (IllegalAccessException ex)
						{
							throw new UncheckedConversionException(ex.getMessage());
						}
					}
				}
				return result;
			}
		}

		return JsonString.of(gate.converter.Converter.toString(obj));
	}

	/**
	 * Creates a JsonElement for the specified object.
	 * <p>
	 * Number, Array and Collection objects will be converted respectively to JsonNumber, JsonArray and JsonArray objects.
	 * <p>
	 * null references will be converted to a JsonString representing an undefined value.
	 * <p>
	 * Other object types will be formatted as JsonString objects using the associated Converter.toText method
	 *
	 * @param obj the object to be formatted
	 *
	 * @return a JsonElement representing the specified object
	 */
	static JsonElement toText(Object obj)
	{
		if (obj == null)
			return UNDEFINED;
		if (obj instanceof Number)
			return JsonNumber.of((Number) obj);
		if (obj instanceof Collection<?>)
			return JsonArray.format((Collection<?>) obj);
		if (obj instanceof Object[])
			return JsonArray.format((Object[]) obj);

		return JsonString.of(gate.converter.Converter.toText(obj));
	}

	public static JsonElement valueOf(String string)
	{
		try
		{
			return parse(string);
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionException(ex);
		}
	}
}
