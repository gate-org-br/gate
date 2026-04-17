package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import gate.util.Reflection;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Represents any JSON element.
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public interface JsonElement extends Serializable
{

	JsonString UNDEFINED = JsonString.of("");

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
			if (obj instanceof Type type)
				return type;
			if (obj instanceof JsonElement jsonElement)
				return jsonElement.getType();

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

	/**
	 * Parses a JSON string into a JsonElement.
	 *
	 * @param string The JSON string to be parsed
	 * @return The parsed JsonElement
	 */
	static JsonElement parse(String string)
			throws ConversionException
	{
		try (JsonParser parser = new JsonParser(new StringReader(string)))
		{
			return parser.parse().orElseThrow();
		}
	}

	/**
	 * Formats the specified JsonElement on JSON notation.
	 * <p>
	 * If the specified JsonElement is a JsonArray or a JsonObject, it's elements will be formatted recursively as their
	 * respective elements on JSON notation.
	 *
	 * @param element the JsonElement to be formatted on JSON notation
	 * @return the specified JsonElement formatted using JSON notation
	 * @throws NullPointerException if any parse the parameters is null
	 */
	static String stringify(JsonElement element)
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
			throw new UncheckedIOException(ex);
		}
	}

	<T> T toObject(Class<T> type);

	<T> T toObject(java.lang.reflect.Type type,
	               java.lang.reflect.Type elementType);

	/**
	 * Creates a {@link JsonElement} representation for the specified object.
	 * <p>
	 * Resolution follows this order:
	 * null values become {@link JsonNull};
	 * {@link Jsonable} values provide their own representation;
	 * booleans, numbers and strings become their respective JSON scalar types;
	 * collections and object arrays become {@link JsonArray}.
	 * <p>
	 * If none of the cases above apply and the object class declares a no-argument constructor,
	 * this method falls back to reflective field-based conversion, producing a {@link JsonObject}
	 * from the non-static, non-null fields of the object.
	 * <p>
	 * This reflective conversion is a convenience fallback. It should not be treated as a stable
	 * domain mapping contract for arbitrary object types.
	 *
	 * @param obj the object to be converted
	 * @return a JsonElement representing the specified object
	 */
	static JsonElement of(Object obj) throws ConversionException
	{
		if (obj == null)
			return JsonNull.INSTANCE;
		if (obj instanceof Jsonable jsonSerializable)
			return jsonSerializable.toJson();
		if (obj instanceof JsonElement jsonElement)
			return jsonElement;

		if (obj instanceof Boolean aBoolean)
			return JsonBoolean.of(aBoolean);
		if (obj instanceof Number number)
			return JsonNumber.of(number);
		if (obj instanceof String string)
			return JsonString.of(string);

		if (obj instanceof Collection<?> collection)
			return JsonArray.of(collection);
		if (obj instanceof Object[] objects)
			return JsonArray.of(objects);

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
							throw new ConversionException(ex.getMessage());
						}
					}
				}
				return result;
			}
		}

		return JsonString.of(gate.converter.Converter.toString(obj));
	}

	/**
	 * Creates a human-oriented {@link JsonElement} representation for the specified object.
	 * <p>
	 * Resolution follows this order:
	 * null values become {@link #UNDEFINED};
	 * {@link Jsonable} values provide their own text-oriented representation;
	 * numbers become {@link JsonNumber};
	 * collections and object arrays become formatted {@link JsonArray} values.
	 * <p>
	 * For other object types, this method falls back to a {@link JsonString}
	 * built from {@link gate.converter.Converter#toText(Object)}.
	 * <p>
	 * This method still returns a {@link JsonElement}, but it may favor
	 * readability over faithful reconstruction of the original object.
	 *
	 * @param obj the object to be formatted
	 * @return a human-oriented JsonElement representing the specified object
	 */
	static JsonElement format(Object obj)
	{
		if (obj == null)
			return UNDEFINED;

		if (obj instanceof JsonElement jsonElement)
			return jsonElement;

		if (obj instanceof Jsonable jsonSerializable)
			return jsonSerializable.toJsonText();

		if (obj instanceof Number number)
			return JsonNumber.format(number);
		if (obj instanceof Boolean bool)
			return JsonBoolean.format(bool);
		if (obj instanceof Collection<?> collection)
			return JsonArray.format(collection);
		if (obj instanceof Object[] objects)
			return JsonArray.format(objects);

		return JsonString.of(gate.converter.Converter.toText(obj));
	}

	/**
	 * Parses a JSON string into a JsonElement.
	 *
	 * @param string The JSON string to be parsed
	 * @return The parsed JsonElement
	 */
	static JsonElement valueOf(String string)
	{
		return parse(string);
	}
}