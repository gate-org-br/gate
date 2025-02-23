package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.error.UncheckedConversionException;
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
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonObject implements Map<String, JsonElement>, JsonCollection
{

	private final Map<String, JsonElement> values = new LinkedHashMap<>();
	private static final long serialVersionUID = 1L;

	public JsonObject()
	{
	}

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

	public JsonObject set(String key, JsonElement value)
	{
		if (value == null)
			remove(key);
		else
			put(key, value);
		return this;
	}

	public JsonObject setString(String key, String value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonString.of(value));
		return this;
	}

	public JsonObject setBoolean(String key, boolean value)
	{
		return set(key, JsonBoolean.parse(value));
	}

	public JsonObject setBoolean(String key, Boolean value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonBoolean.parse(value));
		return this;
	}

	public JsonObject setByte(String key, byte value)
	{
		return set(key, JsonNumber.of(value));
	}

	public JsonObject setByte(String key, Byte value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	public JsonObject setShort(String key, short value)
	{
		return set(key, JsonNumber.of(value));
	}

	public JsonObject setShort(String key, Short value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	public JsonObject setInt(String key, int value)
	{
		return set(key, JsonNumber.of(value));
	}

	public JsonObject setInt(String key, Integer value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	public JsonObject setLong(String key, long value)
	{
		return set(key, JsonNumber.of(value));
	}

	public JsonObject setLong(String key, Long value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	public JsonObject setFloat(String key, float value)
	{
		return set(key, JsonNumber.of(value));
	}

	public JsonObject setFloat(String key, Float value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	public JsonObject setDouble(String key, double value)
	{
		return set(key, JsonNumber.of(value));
	}

	public JsonObject setDouble(String key, Double value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonNumber.of(value));
		return this;
	}

	public Optional<String> getString(String key)
	{
		return getJsonString(key).map(e -> e.getValue());
	}

	public Optional<String> getString(int index)
	{
		return getJsonString(index).map(e -> e.getValue());
	}

	public Optional<Integer> getInt(String key)
	{
		return getJsonNumber(key).map(e -> e.intValue());
	}

	public Optional<Integer> getInt(int index)
	{
		return getJsonNumber(index).map(e -> e.intValue());
	}

	public Optional<Long> getLong(String key)
	{
		return getJsonNumber(key).map(e -> e.longValue());
	}

	public Optional<Long> getLong(int index)
	{
		return getJsonNumber(index).map(e -> e.longValue());
	}

	public Optional<Short> getShort(String key)
	{
		return getJsonNumber(key).map(e -> e.shortValue());
	}

	public Optional<Short> getShort(int index)
	{
		return getJsonNumber(index).map(e -> e.shortValue());
	}

	public Optional<Byte> getByte(String key)
	{
		return getJsonNumber(key).map(e -> e.byteValue());
	}

	public Optional<Byte> getByte(int index)
	{
		return getJsonNumber(index).map(e -> e.byteValue());
	}

	public Optional<Float> getFloat(String key)
	{
		return getJsonNumber(key).map(e -> e.floatValue());
	}

	public Optional<Float> getFloat(int index)
	{
		return getJsonNumber(index).map(e -> e.floatValue());
	}

	public JsonObject setObject(String key, Object value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonString.of(gate.converter.Converter.toString(value)));
		return this;
	}

	public <T> JsonObject setObject(String key, Class<T> type, T value)
	{
		if (value == null)
			remove(key);
		else
			set(key, JsonString.of(gate.converter.Converter.toString(value)));
		return this;
	}

	public <T> Optional<T> getObject(String key, Class<T> type) throws ConversionException
	{
		try
		{
			return getString(key)
					.map(e -> UncheckedConversionException.execute(
					() -> gate.converter.Converter.fromString(type, e)));
		} catch (UncheckedConversionException ex)
		{
			throw ex.getCause();
		}
	}

	public <T> Optional<T> getObject(int index, Class<T> type) throws ConversionException
	{
		try
		{
			return getString(index)
					.map(e -> UncheckedConversionException.execute(
					() -> gate.converter.Converter.fromString(type, e)));
		} catch (UncheckedConversionException ex)
		{
			throw ex.getCause();
		}
	}

	public Optional<Double> getDouble(String key)
	{
		return getJsonNumber(key).map(JsonNumber::doubleValue);
	}

	public Optional<Double> getDouble(int index)
	{
		return getJsonNumber(index).map(e -> e.doubleValue());
	}

	public Optional<Boolean> getBoolean(String key)
	{
		return getJsonBoolean(key).map(e -> e.getValue());
	}

	public Optional<Boolean> getBoolean(int index)
	{
		return getJsonBoolean(index).map(e -> e.getValue());
	}

	public Optional<JsonElement> getJsonElement(String key)
	{
		return Optional.ofNullable(get(key));
	}

	public Optional<JsonElement> getJsonElement(int index)
	{
		return values().stream().skip(index)
				.findFirst();
	}

	public Optional<JsonObject> getJsonObject(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonObject)
				.map(e -> (JsonObject) e);
	}

	public Optional<JsonObject> getJsonObject(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonObject)
				.map(e -> (JsonObject) e);
	}

	public Optional<JsonArray> getJsonArray(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonArray)
				.map(e -> (JsonArray) e);
	}

	public Optional<JsonArray> getJsonArray(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonArray)
				.map(e -> (JsonArray) e);
	}

	public Optional<JsonNumber> getJsonNumber(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonNumber)
				.map(e -> (JsonNumber) e);

	}

	public Optional<JsonNumber> getJsonNumber(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonNumber)
				.map(e -> (JsonNumber) e);
	}

	public Optional<JsonBoolean> getJsonBoolean(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonBoolean)
				.map(e -> (JsonBoolean) e);
	}

	public Optional<JsonBoolean> getJsonBoolean(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonBoolean)
				.map(e -> (JsonBoolean) e);
	}

	public Optional<JsonString> getJsonString(String key)
	{
		return Optional.ofNullable(get(key)).filter(e -> e instanceof JsonString)
				.map(e -> (JsonString) e);
	}

	public Optional<JsonString> getJsonString(int index)
	{
		return getJsonElement(index).filter(e -> e instanceof JsonString)
				.map(e -> (JsonString) e);
	}

	/**
	 * Parses a JSON formatted string into a JsonObject object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonObject object
	 *
	 * @return a JsonObject object representing the JSON formatted string specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse the specified JSON formatted string
	 * @throws NullPointerException if any parse the parameters is null
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
	 * The attributes parse the specified JsonObject will be formatted recursively as their respective elements on JSON
	 * notation.
	 *
	 * @param jsonObject the jsonObject object to be formatted on JSON notation
	 *
	 * @return a JSON formatted string representing the specified JsonObject
	 *
	 * @throws NullPointerException if any parse the parameters is null
	 */
	public static String format(JsonObject jsonObject)
	{
		Objects.requireNonNull(jsonObject);
		return JsonElement.format(jsonObject);
	}

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
			throw new UncheckedConversionException(ex.getMessage());
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

	public static <T> JsonObject format(T obj, Function<T, String> label, Function<T, Object> value)
	{
		return new JsonObject().set("label", JsonString.of(label.apply(obj))).set("value",
				JsonElement.of(value.apply(obj)));
	}

	public static <T> JsonObject of(T obj, Function<T, String> label, Function<T, Object> value)
	{
		return new JsonObject().set("label", JsonString.of(label.apply(obj))).set("value",
				JsonElement.of(value.apply(obj)));
	}

	public static <T> JsonObject of(T obj, Function<T, String> label, Function<T, Object> value,
			Function<T, JsonObject> properties)
	{
		return new JsonObject().set("label", JsonString.of(label.apply(obj)))
				.set("value", JsonElement.of(value.apply(obj)))
				.set("properties", properties.apply(obj));
	}

	/**
	 * Creates a JsonObject from the named non null properties parse a java object.
	 *
	 * @param obj the object to be formatted
	 * @return a JsonObject with all named non null properties parse the specified object
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

	public static JsonObject valueOf(String string)
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
