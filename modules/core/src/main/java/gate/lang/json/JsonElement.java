package gate.lang.json;

import gate.adapter.jsonConverter.JsonConverter;
import gate.adapter.jsonRenderer.JsonRenderer;
import gate.adapter.renderer.Renderer;

import gate.error.ConversionException;
import gate.util.Reflection;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents any JSON element.
 */
public interface JsonElement extends Serializable
{

	JsonString UNDEFINED = JsonString.wrap("");

	/**
	 * Gets the JSON element's type.
	 *
	 * @return the JSON element's type
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
	 *
	 * @param element the JsonElement to be formatted on JSON notation
	 * @return the specified JsonElement formatted using JSON notation
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
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Resolves a single child path segment from this JSON element.
	 * <p>
	 * The default behavior is non-throwing and returns {@link JsonNull#INSTANCE}
	 * when the segment cannot be resolved.
	 * <p>
	 * Concrete implementations may interpret the segment according to their
	 * structure, for example, object property names.
	 *
	 * @param name the path segment to resolve
	 * @return the resolved child element, or {@link JsonNull#INSTANCE} when the
	 * segment cannot be resolved
	 */
	default JsonElement path(String name) {return JsonNull.INSTANCE;}

	/**
	 * Resolves a single child path segment from this JSON element by array
	 * index.
	 * <p>
	 * The default behavior is non-throwing and returns {@link JsonNull#INSTANCE}
	 * when the segment cannot be resolved.
	 *
	 * @param index the array index to resolve
	 * @return the resolved child element, or {@link JsonNull#INSTANCE} when the
	 * index cannot be resolved
	 */
	default JsonElement path(int index) {return JsonNull.INSTANCE;}

	/**
	 * Converts this JSON element to an instance of the specified Java type.
	 * <p>
	 * This overload is intended for typed materialization, such as converting
	 * a JSON object into a domain object or a scalar JSON value into a specific
	 * boxed Java type.
	 *
	 * @param <T>  the target Java type
	 * @param type the target Java type
	 * @return the converted Java object
	 */
	<T> T decode(Class<T> type);

	/**
	 * Converts this JSON element to a parameterized Java type.
	 * <p>
	 * This overload is primarily used for collection-like structures where
	 * element type information is available from the generic type.
	 *
	 * @param <T>  the target Java type
	 * @param type the target generic Java type
	 * @return the converted Java object
	 */
	@SuppressWarnings("unchecked")
	default <T> T decode(java.lang.reflect.Type type)
	{
		return decode((Class<T>) Reflection.getRawType(type));
	}

	/**
	 * Converts this JSON element to its natural Java representation.
	 * <p>
	 * Implementations should return the closest Java structural equivalent of
	 * the JSON value:
	 * strings as {@link String}, numbers as {@link Number}, booleans as
	 * {@link Boolean}, arrays as {@link java.util.List}, objects as
	 * {@link java.util.Map}, and {@code null} for JSON null.
	 *
	 * @return the natural Java representation of this JSON element
	 */
	Object unwrap();

	/**
	 * Wraps a naturally representable Java value as a {@link JsonElement}.
	 * <p>
	 * Accepted inputs are:
	 * {@code null}, {@link JsonElement}, {@link String}, {@link Number},
	 * {@link Boolean}, {@link Map}, {@link Collection}, and object arrays.
	 * <p>
	 * Any other type is rejected with a {@link ConversionException}.
	 *
	 * @param obj the Java value to wrap
	 * @return the wrapped JsonElement
	 * @throws ConversionException when the value is not naturally representable
	 *                             as JSON
	 */
	static JsonElement wrap(Object obj) throws ConversionException
	{
		if (obj == null)
			return JsonNull.INSTANCE;
		if (obj instanceof JsonElement jsonElement)
			return jsonElement;

		if (obj instanceof Boolean aBoolean)
			return JsonBoolean.wrap(aBoolean);
		if (obj instanceof Number number)
			return JsonNumber.wrap(number);
		if (obj instanceof String string)
			return JsonString.wrap(string);
		if (obj instanceof Collection<?> collection)
			return JsonArray.wrap(collection);
		if (obj instanceof Object[] objects)
			return JsonArray.wrap(objects);
		if (obj instanceof Map<?, ?> map)
		{
			JsonObject result = new JsonObject();
			for (Map.Entry<?, ?> entry : map.entrySet())
			{
				if (!(entry.getKey() instanceof String key))
					throw new ConversionException("Can't wrap map with non-string key: %s".formatted(entry.getKey()));
				result.set(key, wrap(entry.getValue()));
			}
			return result;
		}

		throw new ConversionException("Can't wrap %s as JsonElement".formatted(obj.getClass().getName()));
	}

	/**
	 * Creates a {@link JsonElement} representation for the specified object.
	 * <p>
	 * Resolution follows this order:
	 * null values become {@link JsonNull};
	 * {@link JsonElement} values are returned as-is;
	 * booleans, numbers and strings become their respective JSON scalar types;
	 * collections and object arrays become {@link JsonArray};
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
	static JsonElement encode(Object obj) throws ConversionException
	{
		return JsonConverter.toJson(obj);
	}

	/**
	 * Creates a human-oriented {@link JsonElement} representation for the specified object.
	 * <p>
	 * Resolution follows this order:
	 * null values become {@link #UNDEFINED};
	 * {@link JsonElement} values are returned as-is;
	 * numbers become {@link JsonNumber};
	 * collections and object arrays become formatted {@link JsonArray} values;
	 * <p>
	 * For other object types, this method falls back to a {@link JsonString}
	 * built from {@link Renderer#render(Object)}.
	 * <p>
	 * This method still returns a {@link JsonElement}, but it may favor
	 * readability over faithful reconstruction of the original object.
	 *
	 * @param obj the object to be formatted
	 * @return a human-oriented JsonElement representing the specified object
	 */
	static JsonElement render(Object obj)
	{
		if (obj == null)
			return UNDEFINED;

		if (obj instanceof JsonElement jsonElement)
			return jsonElement;

		if (obj instanceof Number number)
			return JsonNumber.render(number);
		if (obj instanceof Boolean bool)
			return JsonBoolean.render(bool);

		return JsonRenderer.render(obj);
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

	default Optional<JsonElement> getProperty(String property)
	{
		if (!"this".equals(property))
			throw new IllegalArgumentException(property + " is not a valid property");
		return Optional.of(this);
	}
}