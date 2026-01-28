package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import gate.lang.property.Property;
import gate.util.Reflection;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a JSON object as a java Map.
 *
 * <p>
 * This class provides a comprehensive API for creating, manipulating, and
 * converting JSON objects in Java. It implements the {@link Map} interface with
 * String keys and {@link JsonElement} values, allowing standard Java collection
 * operations.</p>
 *
 * <p>
 * The class supports:</p>
 *
 * <ul>
 * <li>Type-safe getters and setters for primitive types</li>
 * <li>Conversion to and from Java objects</li>
 * <li>JSON parsing and formatting</li>
 * <li>Optional-based value retrieval to handle missing keys safely</li>
 * </ul>
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonObject implements Map<String, JsonElement>, JsonCollection
{

	private final Map<String, JsonElement> values = new LinkedHashMap<>();
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty JsonObject.
	 */
	public JsonObject()
	{
	}

	/**
	 * Creates a JsonObject with the specified initial values.
	 *
	 * @param values the initial key-value pairs to populate this JsonObject
	 */
	public JsonObject(Map<String, JsonElement> values)
	{
		this.values.putAll(values);
	}

	@Override
	public Type getType()
	{
		return Type.OBJECT;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof JsonObject && values.equals(((JsonObject) o).values);
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String toString()
	{
		return JsonObject.format(this);
	}

	/**
	 * Sets a JsonElement value for the specified key. If the value is null,
	 * the key is removed.
	 *
	 * @param key the key
	 * @param value the JsonElement value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject set(String key, JsonElement value)
	{
		if (value == null)
			remove(key);
		else
			put(key, value);
		return this;
	}

	/**
	 * Sets a String value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the String value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setString(String key, String value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonString.of(value));
		return this;
	}

	/**
	 * Sets a boolean value for the specified key.
	 *
	 * @param key the key
	 * @param value the boolean value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setBoolean(String key, boolean value)
	{
		return set(key, JsonBoolean.parse(value));
	}

	/**
	 * Sets a Boolean value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the Boolean value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setBoolean(String key, Boolean value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonBoolean.parse(value));
		return this;
	}

	/**
	 * Sets a byte value for the specified key.
	 *
	 * @param key the key
	 * @param value the byte value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setByte(String key, byte value)
	{
		return set(key, JsonNumber.of(value));
	}

	/**
	 * Sets a Byte value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the Byte value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setByte(String key, Byte value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	/**
	 * Sets a short value for the specified key.
	 *
	 * @param key the key
	 * @param value the short value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setShort(String key, short value)
	{
		return set(key, JsonNumber.of(value));
	}

	/**
	 * Sets a Short value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the Short value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setShort(String key, Short value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	/**
	 * Sets an int value for the specified key.
	 *
	 * @param key the key
	 * @param value the int value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setInt(String key, int value)
	{
		return set(key, JsonNumber.of(value));
	}

	/**
	 * Sets an Integer value for the specified key. If the value is null,
	 * the key is removed.
	 *
	 * @param key the key
	 * @param value the Integer value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setInt(String key, Integer value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	/**
	 * Sets a long value for the specified key.
	 *
	 * @param key the key
	 * @param value the long value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setLong(String key, long value)
	{
		return set(key, JsonNumber.of(value));
	}

	/**
	 * Sets a Long value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the Long value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setLong(String key, Long value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	/**
	 * Sets a float value for the specified key.
	 *
	 * @param key the key
	 * @param value the float value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setFloat(String key, float value)
	{
		return set(key, JsonNumber.of(value));
	}

	/**
	 * Sets a Float value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the Float value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setFloat(String key, Float value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	/**
	 * Sets a double value for the specified key.
	 *
	 * @param key the key
	 * @param value the double value to set
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setDouble(String key, double value)
	{
		return set(key, JsonNumber.of(value));
	}

	/**
	 * Sets a Double value for the specified key. If the value is null, the
	 * key is removed.
	 *
	 * @param key the key
	 * @param value the Double value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setDouble(String key, Double value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	/**
	 * Gets a String value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the String value, or empty if not
	 * present or not a String
	 */
	public Optional<String> getString(String key)
	{
		return getJsonString(key).map(e -> e.getValue());
	}

	/**
	 * Gets a String value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the String value, or empty if not
	 * present or not a String
	 */
	public Optional<String> getString(int index)
	{
		return getJsonString(index).map(e -> e.getValue());
	}

	/**
	 * Gets an Integer value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Integer value, or empty if not
	 * present or not a Number
	 */
	public Optional<Integer> getInt(String key)
	{
		return getJsonNumber(key).map(e -> e.intValue());
	}

	/**
	 * Gets an Integer value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Integer value, or empty if not
	 * present or not a Number
	 */
	public Optional<Integer> getInt(int index)
	{
		return getJsonNumber(index).map(e -> e.intValue());
	}

	/**
	 * Gets a Long value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Long value, or empty if not
	 * present or not a Number
	 */
	public Optional<Long> getLong(String key)
	{
		return getJsonNumber(key).map(e -> e.longValue());
	}

	/**
	 * Gets a Long value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Long value, or empty if not
	 * present or not a Number
	 */
	public Optional<Long> getLong(int index)
	{
		return getJsonNumber(index).map(e -> e.longValue());
	}

	/**
	 * Gets a Short value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Short value, or empty if not
	 * present or not a Number
	 */
	public Optional<Short> getShort(String key)
	{
		return getJsonNumber(key).map(e -> e.shortValue());
	}

	/**
	 * Gets a Short value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Short value, or empty if not
	 * present or not a Number
	 */
	public Optional<Short> getShort(int index)
	{
		return getJsonNumber(index).map(e -> e.shortValue());
	}

	/**
	 * Gets a Byte value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Byte value, or empty if not
	 * present or not a Number
	 */
	public Optional<Byte> getByte(String key)
	{
		return getJsonNumber(key).map(e -> e.byteValue());
	}

	/**
	 * Gets a Byte value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Byte value, or empty if not
	 * present or not a Number
	 */
	public Optional<Byte> getByte(int index)
	{
		return getJsonNumber(index).map(e -> e.byteValue());
	}

	/**
	 * Gets a Float value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Float value, or empty if not
	 * present or not a Number
	 */
	public Optional<Float> getFloat(String key)
	{
		return getJsonNumber(key).map(e -> e.floatValue());
	}

	/**
	 * Gets a Float value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Float value, or empty if not
	 * present or not a Number
	 */
	public Optional<Float> getFloat(int index)
	{
		return getJsonNumber(index).map(e -> e.floatValue());
	}

	/**
	 * Sets an Object value for the specified key by converting it to a
	 * String. If the value is null, the key is removed.
	 *
	 * @param key the key
	 * @param value the Object value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public JsonObject setObject(String key, Object value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonString.of(gate.converter.Converter.toString(value)));
		return this;
	}

	/**
	 * Sets a typed Object value for the specified key by converting it to a
	 * String. If the value is null, the key is removed.
	 *
	 * @param <T> the type of the object
	 * @param key the key
	 * @param type the class of the object
	 * @param value the Object value to set, or null to remove the key
	 * @return this JsonObject for method chaining
	 */
	public <T> JsonObject setObject(String key, Class<T> type, T value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonString.of(gate.converter.Converter.toString(value)));
		return this;
	}

	/**
	 * Gets an Object value for the specified key by converting from String.
	 *
	 * @param <T> the type to convert to
	 * @param key the key
	 * @param type the class to convert to
	 * @return an Optional containing the converted Object, or empty if not
	 * present
	 * @throws ConversionException if conversion fails
	 */
	public <T> Optional<T> getObject(String key, Class<T> type) throws ConversionException
	{
		return getString(key)
			.map(e -> gate.converter.Converter.fromString(type, e));
	}

	/**
	 * Gets an Object value at the specified index by converting from
	 * String.
	 *
	 * @param <T> the type to convert to
	 * @param index the index
	 * @param type the class to convert to
	 * @return an Optional containing the converted Object, or empty if not
	 * present
	 * @throws ConversionException if conversion fails
	 */
	public <T> Optional<T> getObject(int index, Class<T> type) throws ConversionException
	{
		return getString(index)
			.map(e -> gate.converter.Converter.fromString(type, e));
	}

	/**
	 * Gets a Double value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Double value, or empty if not
	 * present or not a Number
	 */
	public Optional<Double> getDouble(String key)
	{
		return getJsonNumber(key).map(JsonNumber::doubleValue);
	}

	/**
	 * Gets a Double value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Double value, or empty if not
	 * present or not a Number
	 */
	public Optional<Double> getDouble(int index)
	{
		return getJsonNumber(index).map(e -> e.doubleValue());
	}

	/**
	 * Gets a Boolean value for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the Boolean value, or empty if not
	 * present or not a Boolean
	 */
	public Optional<Boolean> getBoolean(String key)
	{
		return getJsonBoolean(key).map(e -> e.getValue());
	}

	/**
	 * Gets a Boolean value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the Boolean value, or empty if not
	 * present or not a Boolean
	 */
	public Optional<Boolean> getBoolean(int index)
	{
		return getJsonBoolean(index).map(e -> e.getValue());
	}

	/**
	 * Gets a JsonElement for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the JsonElement, or empty if not
	 * present
	 */
	public Optional<JsonElement> getJsonElement(String key)
	{
		return Optional.ofNullable(get(key));
	}

	/**
	 * Gets a JsonElement at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonElement, or empty if not
	 * present
	 */
	public Optional<JsonElement> getJsonElement(int index)
	{
		return values().stream().skip(index)
			.findFirst();
	}

	/**
	 * Gets a JsonObject for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the JsonObject, or empty if not
	 * present or not a JsonObject
	 */
	public Optional<JsonObject> getJsonObject(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonObject)
			.map(e -> (JsonObject) e);
	}

	/**
	 * Gets a JsonObject at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonObject, or empty if not
	 * present or not a JsonObject
	 */
	public Optional<JsonObject> getJsonObject(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonObject)
			.map(e -> (JsonObject) e);
	}

	/**
	 * Gets a JsonArray for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the JsonArray, or empty if not present
	 * or not a JsonArray
	 */
	public Optional<JsonArray> getJsonArray(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonArray)
			.map(e -> (JsonArray) e);
	}

	/**
	 * Gets a JsonArray at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonArray, or empty if not present
	 * or not a JsonArray
	 */
	public Optional<JsonArray> getJsonArray(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonArray)
			.map(e -> (JsonArray) e);
	}

	/**
	 * Gets a JsonNumber for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the JsonNumber, or empty if not
	 * present or not a JsonNumber
	 */
	public Optional<JsonNumber> getJsonNumber(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonNumber)
			.map(e -> (JsonNumber) e);

	}

	/**
	 * Gets a JsonNumber at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonNumber, or empty if not
	 * present or not a JsonNumber
	 */
	public Optional<JsonNumber> getJsonNumber(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonNumber)
			.map(e -> (JsonNumber) e);
	}

	/**
	 * Gets a JsonBoolean for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the JsonBoolean, or empty if not
	 * present or not a JsonBoolean
	 */
	public Optional<JsonBoolean> getJsonBoolean(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonBoolean)
			.map(e -> (JsonBoolean) e);
	}

	/**
	 * Gets a JsonBoolean at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonBoolean, or empty if not
	 * present or not a JsonBoolean
	 */
	public Optional<JsonBoolean> getJsonBoolean(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonBoolean)
			.map(e -> (JsonBoolean) e);
	}

	/**
	 * Gets a JsonString for the specified key.
	 *
	 * @param key the key
	 * @return an Optional containing the JsonString, or empty if not
	 * present or not a JsonString
	 */
	public Optional<JsonString> getJsonString(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonString)
			.map(e -> (JsonString) e);
	}

	/**
	 * Gets a JsonString at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonString, or empty if not
	 * present or not a JsonString
	 */
	public Optional<JsonString> getJsonString(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonString)
			.map(e -> (JsonString) e);
	}

	/**
	 * Parses a JSON formatted string into a JsonObject object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonObject
	 * object
	 *
	 * @return a JsonObject object representing the JSON formatted string
	 * specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse
	 * the specified JSON formatted string
	 * @throws NullPointerException if the json parameter is null
	 */
	public static JsonObject parse(String json) throws ConversionException
	{
		Objects.requireNonNull(json);

		JsonElement element = JsonElement.parse(json);
		if (element.getType() != JsonElement.Type.OBJECT)
			throw new ConversionException("the specified JsonElement is not a JsonObject");
		return (JsonObject) element;
	}

	/**
	 * Formats the specified JsonObject into a JSON formatted string.
	 * <p>
	 * The attributes of the specified JsonObject will be formatted
	 * recursively as their respective elements on JSON notation.
	 * </p>
	 *
	 * @param jsonObject the JsonObject object to be formatted on JSON
	 * notation
	 *
	 * @return a JSON formatted string representing the specified JsonObject
	 *
	 * @throws NullPointerException if the jsonObject parameter is null
	 */
	public static String format(JsonObject jsonObject)
	{
		Objects.requireNonNull(jsonObject);
		return JsonElement.format(jsonObject);
	}

	/**
	 * Converts this JsonObject to a Java object of the specified type.
	 * <p>
	 * Uses reflection to create an instance and populate its fields from
	 * the JSON object's properties.
	 * </p>
	 *
	 * @param <T> the type to convert to
	 * @param type the class of the type to convert to
	 * @return an instance of the specified type populated with this
	 * JsonObject's data
	 * @throws ConversionException if conversion fails due to reflection
	 * errors
	 */
	@Override
	public <T> T toObject(Class<T> type)
	{
		try
		{
			Constructor<T> constructor = type.getDeclaredConstructor();
			constructor.setAccessible(true);
			T object = constructor.newInstance();

			for (Map.Entry<String, JsonElement> entry : entrySet())
			{
				if (entry.getValue() != null)
				{
					Field field = Reflection.findField(type, entry.getKey())
						.orElseThrow(() -> new NoSuchFieldException("No such field %s found on type %s".formatted(entry.getKey(), type.getName())));
					field.setAccessible(true);
					field.set(object, entry.getValue().toObject(field.getType(),
						Reflection.getElementType(field.getGenericType())));
				}
			}

			return object;
		} catch (NoSuchMethodException | NoSuchFieldException | InstantiationException
			| IllegalAccessException | InvocationTargetException | SecurityException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, E> T toObject(java.lang.reflect.Type type, java.lang.reflect.Type elementType)
	{
		return toObject((Class<T>) type);
	}

	@Override
	public int size()
	{
		return values.size();
	}

	@Override
	public boolean isEmpty()
	{
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return values.containsValue(value);
	}

	@Override
	public JsonElement get(Object key)
	{
		return values.get(key);
	}

	@Override
	public JsonElement put(String key, JsonElement value)
	{
		return values.put(key, value);
	}

	@Override
	public JsonElement remove(Object key)
	{
		return values.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends JsonElement> m)
	{
		values.putAll(m);
	}

	@Override
	public void clear()
	{
		values.clear();
	}

	@Override
	public Set<String> keySet()
	{
		return values.keySet();
	}

	@Override
	public Collection<JsonElement> values()
	{
		return values.values();
	}

	@Override
	public Set<Entry<String, JsonElement>> entrySet()
	{
		return values.entrySet();
	}

	/**
	 * Creates a JsonObject with label and value properties from the given
	 * object.
	 *
	 * @param <T> the type of the object
	 * @param obj the object to format
	 * @param label function to extract the label
	 * @param value function to extract the value
	 * @return a JsonObject with label and value properties
	 */
	public static <T> JsonObject format(T obj, Function<T, String> label, Function<T, Object> value)
	{
		return new JsonObject().set("label", JsonString.of(label.apply(obj))).set("value",
			JsonElement.of(value.apply(obj)));
	}

	/**
	 * Creates a JsonObject with label and value properties from the given
	 * object.
	 *
	 * @param <T> the type of the object
	 * @param obj the object to format
	 * @param label function to extract the label
	 * @param value function to extract the value
	 * @return a JsonObject with label and value properties
	 */
	public static <T> JsonObject of(T obj, Function<T, String> label, Function<T, Object> value)
	{
		return new JsonObject().set("label", JsonString.of(label.apply(obj))).set("value",
			JsonElement.of(value.apply(obj)));
	}

	/**
	 * Creates a JsonObject with label, value, and properties from the given
	 * object.
	 *
	 * @param <T> the type of the object
	 * @param obj the object to format
	 * @param label function to extract the label
	 * @param value function to extract the value
	 * @param properties function to extract additional properties as a
	 * JsonObject
	 * @return a JsonObject with label, value, and properties
	 */
	public static <T> JsonObject of(T obj, Function<T, String> label, Function<T, Object> value,
		Function<T, JsonObject> properties)
	{
		return new JsonObject().set("label", JsonString.of(label.apply(obj)))
			.set("value", JsonElement.of(value.apply(obj)))
			.set("properties", properties.apply(obj));
	}

	/**
	 * Creates a JsonObject from the named non-null properties of a Java
	 * object.
	 * <p>
	 * Uses the Property API to extract properties with display names and
	 * converts their values to text representation.
	 * </p>
	 *
	 * @param obj the object to be formatted
	 * @return a JsonObject with all named non-null properties of the
	 * specified object
	 */
	public static JsonObject format(Object obj)
	{
		JsonObject result = new JsonObject();
		Property.getProperties(obj.getClass()).forEach((property) ->
		{
			String name = property.getDisplayName();
			if (name != null)
			{
				Object value = property.getValue(obj);
				if (value != null)
					result.setString(name, gate.converter.Converter.toText(value));
			}
		});

		return result;
	}

	/**
	 * Parses a JSON string into a JsonObject. This is an alias for
	 * {@link #parse(String)}.
	 *
	 * @param string the JSON formatted string to parse
	 * @return a JsonObject representing the parsed JSON
	 */
	public static JsonObject valueOf(String string)
	{
		return parse(string);
	}
}
