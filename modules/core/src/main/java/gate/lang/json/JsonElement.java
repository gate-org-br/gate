package gate.lang.json;

import gate.adapter.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
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
 */
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
			throw new AppError(ex);
		}
	}

	/**
	 * Resolves a single child path segment from this JSON element.
	 * <p>
	 * The default behavior is non-throwing and returns {@link JsonNull#INSTANCE}
	 * when the segment cannot be resolved.
	 * <p>
	 * Concrete implementations may interpret the segment according to their
	 * structure, for example object property names.
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
	 * This overload is primarily used for collection-like structures where the
	 * raw type and the element type must both be provided.
	 *
	 * @param <T>         the target Java type
	 * @param type        the target raw Java type
	 * @param elementType the generic element type associated with {@code type}
	 * @return the converted Java object
	 */
	<T> T decode(java.lang.reflect.Type type,
	             java.lang.reflect.Type elementType);

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
			return JsonBoolean.of(aBoolean);
		if (obj instanceof Number number)
			return JsonNumber.of(number);
		if (obj instanceof String string)
			return JsonString.of(string);
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
					throw new ConversionException("Can't wrap map with non-string key: %s", entry.getKey());
				result.set(key, wrap(entry.getValue()));
			}
			return result;
		}

		throw new ConversionException("Can't wrap %s as JsonElement", obj.getClass().getName());
	}

	/**
	 * Creates a {@link JsonElement} representation for the specified object.
	 * <p>
	 * Resolution follows this order:
	 * null values become {@link JsonNull};
	 * {@link JsonElement} values are returned as-is;
	 * booleans, numbers and strings become their respective JSON scalar types;
	 * collections and object arrays become {@link JsonArray};
	 * types associated with a {@link JsonAdapter} are adapted through it.
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
		if (obj == null)
			return JsonNull.INSTANCE;
		if (obj instanceof JsonElement jsonElement)
			return jsonElement;
		if (obj instanceof Boolean aBoolean)
			return JsonBoolean.of(aBoolean);
		if (obj instanceof Number number)
			return JsonNumber.of(number);
		if (obj instanceof String string)
			return JsonString.of(string);
		if (obj instanceof Map<?, ?> map)
		{
			JsonObject result = new JsonObject();
			for (Map.Entry<?, ?> entry : map.entrySet())
			{
				if (!(entry.getKey() instanceof String key))
					throw new ConversionException("Can't encode map with non-string key: %s", entry.getKey());
				result.set(key, encode(entry.getValue()));
			}
			return result;
		}

		var jsonAdapter = JsonAdapter.of((Class<Object>) obj.getClass());
		if (jsonAdapter != null)
			return jsonAdapter.toJson(obj);

		if (obj instanceof Collection<?> collection)
			return collection.stream().map(JsonElement::encode)
					.collect(java.util.stream.Collectors.toCollection(JsonArray::new));
		if (obj instanceof Object[] objects)
			return java.util.stream.Stream.of(objects).map(JsonElement::encode)
					.collect(java.util.stream.Collectors.toCollection(JsonArray::new));

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
								result.put(field.getName(), JsonElement.encode(value));
						} catch (IllegalAccessException ex)
						{
							throw new ConversionException(ex.getMessage());
						}
					}
				}
				return result;
			}
		}

		return JsonString.of(Converter.toString(obj));
	}

	/**
	 * Creates a human-oriented {@link JsonElement} representation for the specified object.
	 * <p>
	 * Resolution follows this order:
	 * null values become {@link #UNDEFINED};
	 * {@link JsonElement} values are returned as-is;
	 * numbers become {@link JsonNumber};
	 * collections and object arrays become formatted {@link JsonArray} values;
	 * types associated with a {@link JsonAdapter} are adapted through it.
	 * <p>
	 * For other object types, this method falls back to a {@link JsonString}
	 * built from {@link Converter#render(Object)}.
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

		var jsonAdapter = JsonAdapter.of((Class<Object>) obj.getClass());
		if (jsonAdapter != null)
			return jsonAdapter.toJsonText(obj);

		if (obj instanceof Collection<?> collection)
			return JsonArray.render(collection);
		if (obj instanceof Object[] objects)
			return JsonArray.render(objects);

		return JsonString.of(Converter.render(obj));
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