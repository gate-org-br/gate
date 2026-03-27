package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import gate.util.Reflection;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a JSON array as a List of JsonElement.
 *
 * <p>
 * This class provides a comprehensive API for creating, manipulating, and
 * converting JSON arrays in Java. It implements the {@link List} interface with
 * {@link JsonElement} elements, allowing standard Java collection
 * operations.</p>
 *
 * <p>
 * The class supports:</p>
 *
 * <ul>
 * <li>Type-safe adders for primitive types</li>
 * <li>Conversion to and from Java collections</li>
 * <li>JSON parsing and formatting</li>
 * <li>Optional-based value retrieval to handle missing indices safely</li>
 * <li>Stream-based factory methods for functional programming</li>
 * </ul>
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonArray implements List<JsonElement>, JsonCollection
{

	private final List<JsonElement> values = new ArrayList<>();

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty JsonArray.
	 */
	public JsonArray()
	{
	}

	/**
	 * Creates a JsonArray with the specified initial elements.
	 *
	 * @param c the collection of JsonElements to populate this JsonArray
	 */
	public JsonArray(Collection<? extends JsonElement> c)
	{
		values.addAll(c);
	}

	@Override
	public Type getType()
	{
		return Type.ARRAY;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof JsonArray && values.equals(((JsonArray) o).values);
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String toString()
	{
		return JsonArray.format(this);
	}

	/**
	 * Converts this JsonArray to a Java object of the specified type.
	 * <p>
	 * This method cannot be used without specifying an element type. Use
	 * {@link #toObject(java.lang.reflect.Type, java.lang.reflect.Type)}
	 * instead.
	 * </p>
	 *
	 * @param <T>  the type to convert to
	 * @param type the class of the type to convert to
	 * @return never returns normally
	 * @throws UnsupportedOperationException always thrown as element type
	 *                                       is required
	 */
	@Override
	public <T> T toObject(Class<T> type)
	{
		throw new UnsupportedOperationException("Can't create java object from json array without element type");
	}

	/**
	 * Converts this JsonArray to a Java collection of the specified type
	 * with element type information.
	 * <p>
	 * Supports conversion to {@link Set} or {@link List} based on the
	 * specified type.
	 * </p>
	 *
	 * @param <T>         the collection type to convert to
	 * @param type        the collection type to convert to
	 * @param elementType the element type for the collection
	 * @return a collection of the specified type populated with this
	 * JsonArray's elements
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T toObject(java.lang.reflect.Type type, java.lang.reflect.Type elementType)
	{

		Class<T> clazz = (Class<T>) type;

		if (clazz.isAssignableFrom(Set.class))
			return (T) stream()
					.map(e -> e.toObject(Reflection.getRawType(elementType), Reflection.getElementType(elementType)))
					.collect(Collectors.toSet());

		return (T) stream()
				.map(e -> e.toObject(Reflection.getRawType(elementType), Reflection.getElementType(elementType)))
				.collect(Collectors.toList());

	}

	/**
	 * Parses a JSON formatted string into a JsonArray object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonArray
	 *             object
	 * @return a JsonArray object representing the JSON formatted string
	 * specified
	 * @throws ConversionException  if an error occurs while trying to parse
	 *                              the specified JSON formatted string
	 * @throws NullPointerException if the JSON parameter is null
	 */
	public static JsonArray parse(String json) throws ConversionException
	{
		Objects.requireNonNull(json);

		JsonElement element = JsonElement.parse(json);
		if (element.getType() != JsonElement.Type.ARRAY)
			throw new ConversionException("the specified JsonElement is not a JsonArray");
		return (JsonArray) element;
	}

	/**
	 * Formats the specified JsonArray into a JSON formatted string.
	 * <p>
	 * The elements of the specified JsonArray will be formatted recursively
	 * as their respective elements on JSON notation.
	 * </p>
	 *
	 * @param jsonArray the JsonArray object to be formatted on JSON
	 *                  notation
	 * @return a JSON formatted string representing the specified JsonArray
	 * @throws NullPointerException if the jsonArray parameter is null
	 */
	public static String format(JsonArray jsonArray)
	{
		Objects.requireNonNull(jsonArray);
		return JsonElement.stringify(jsonArray);
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
	public boolean contains(Object o)
	{
		return values.contains(o);
	}

	@Override
	public Iterator<JsonElement> iterator()
	{
		return values.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return values.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return values.toArray(a);
	}

	@Override
	public boolean add(JsonElement e)
	{
		return values.add(e);
	}

	@Override
	public boolean remove(Object o)
	{
		return values.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return values.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends JsonElement> c)
	{
		return values.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends JsonElement> c)
	{
		return values.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return values.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return values.retainAll(c);
	}

	@Override
	public void clear()
	{
		values.clear();
	}

	@Override
	public JsonElement get(int index)
	{
		return values.get(index);
	}

	@Override
	public JsonElement set(int index, JsonElement element)
	{
		return values.set(index, element);
	}

	@Override
	public void add(int index, JsonElement element)
	{
		values.add(index, element);
	}

	@Override
	public JsonElement remove(int index)
	{
		return values.remove(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return values.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return values.lastIndexOf(o);
	}

	@Override
	public ListIterator<JsonElement> listIterator()
	{
		return values.listIterator();
	}

	@Override
	public ListIterator<JsonElement> listIterator(int index)
	{
		return values.listIterator(index);
	}

	@Override
	public List<JsonElement> subList(int fromIndex, int toIndex)
	{
		return values.subList(fromIndex, toIndex);
	}

	/**
	 * Adds a JsonElement to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the JsonElement to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addJsonElement(JsonElement value)
	{
		add(value != null ? value : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a String value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the String value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addString(String value)
	{
		add(value != null ? JsonString.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a byte value to this JsonArray.
	 *
	 * @param value the byte value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addByte(byte value)
	{
		add(JsonNumber.of(value));
		return this;
	}

	/**
	 * Adds a Byte value to this JsonArray. If null, adds JsonNull.INSTANCE.
	 *
	 * @param value the Byte value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addByte(Byte value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a short value to this JsonArray.
	 *
	 * @param value the short value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addShort(short value)
	{
		add(JsonNumber.of(value));
		return this;
	}

	/**
	 * Adds a Short value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the Short value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addShort(Short value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds an int value to this JsonArray.
	 *
	 * @param value the int value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addInt(int value)
	{
		add(JsonNumber.of(value));
		return this;
	}

	/**
	 * Adds an Integer value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the Integer value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addInt(Integer value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a long value to this JsonArray.
	 *
	 * @param value the long value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addLong(long value)
	{
		add(JsonNumber.of(value));
		return this;
	}

	/**
	 * Adds a Long value to this JsonArray. If null, adds JsonNull.INSTANCE.
	 *
	 * @param value the Long value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addLong(Long value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a float value to this JsonArray.
	 *
	 * @param value the float value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addFloat(float value)
	{
		add(JsonNumber.of(value));
		return this;
	}

	/**
	 * Adds a Float value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the Float value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addFloat(Float value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a double value to this JsonArray.
	 *
	 * @param value the double value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addDouble(double value)
	{
		add(JsonNumber.of(value));
		return this;
	}

	/**
	 * Adds a Double value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the Double value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addDouble(Double value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a BigDecimal value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the BigDecimal value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addBigDecimal(BigDecimal value)
	{
		add(value != null ? JsonNumber.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Adds a boolean value to this JsonArray.
	 *
	 * @param value the boolean value to add
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addBoolean(boolean value)
	{
		add(JsonBoolean.of(value));
		return this;
	}

	/**
	 * Adds a Boolean value to this JsonArray. If null, adds
	 * JsonNull.INSTANCE.
	 *
	 * @param value the Boolean value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addBoolean(Boolean value)
	{
		add(value != null ? JsonBoolean.of(value) : JsonNull.INSTANCE);
		return this;
	}

	/**
	 * Gets a String value at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the String value, or empty if index
	 * out of bounds or not a String
	 */
	public Optional<String> getString(int index)
	{
		return values.size() > index && values.get(index) instanceof JsonString string ? Optional.of(string.getValue())
				: Optional.empty();
	}

	/**
	 * Gets a JsonElement at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonElement, or empty if index out
	 * of bounds
	 */
	public Optional<JsonElement> getJsonElement(int index)
	{
		return values.size() > index ? Optional.ofNullable(values.get(index)) : Optional.empty();
	}

	/**
	 * Adds an Object value to this JsonArray by converting it to a String.
	 * If null, adds JsonNull.INSTANCE.
	 *
	 * @param value the Object value to add, or null for JsonNull
	 * @return this JsonArray for method chaining
	 */
	public JsonArray addObject(Object value)
	{
		if (value == null)
			add(JsonNull.INSTANCE);
		else
			add(JsonString.of(gate.converter.Converter.toString(value)));
		return this;
	}

	/**
	 * Creates a JsonArray from a Stream of objects by converting each to a
	 * JsonElement.
	 *
	 * @param stream the stream of objects to convert
	 * @return a JsonArray containing the converted elements
	 */
	public static JsonArray of(Stream<?> stream)
	{
		return stream.map(JsonElement::of).collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray from a Collection of objects by converting each
	 * to a JsonElement.
	 *
	 * @param objects the collection of objects to convert
	 * @return a JsonArray containing the converted elements
	 */
	public static JsonArray of(Collection<?> objects)
	{
		return JsonArray.of(objects.stream());
	}

	/**
	 * Creates a JsonArray from an array of objects by converting each to a
	 * JsonElement.
	 *
	 * @param objects the array of objects to convert
	 * @return a JsonArray containing the converted elements
	 */
	public static JsonArray of(Object... objects)
	{
		return JsonArray.of(Stream.of(objects));
	}

	/**
	 * Creates a JsonArray of JsonObjects with label and value properties
	 * from a List.
	 *
	 * @param <T>     the type of objects in the list
	 * @param objects the list of objects to convert
	 * @param label   function to extract the label from each object
	 * @param value   function to extract the value from each object
	 * @return a JsonArray containing JsonObjects with label and value
	 * properties
	 */
	public static <T> JsonArray of(List<T> objects, Function<T, String> label, Function<T, Object> value)
	{
		return objects.stream().map(e -> JsonObject.of(e, label, value))
				.collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray of JsonObjects with label, value, and properties
	 * from a List.
	 *
	 * @param <T>        the type of objects in the list
	 * @param objects    the list of objects to convert
	 * @param label      function to extract the label from each object
	 * @param value      function to extract the value from each object
	 * @param properties function to extract additional properties as a
	 *                   JsonObject
	 * @return a JsonArray containing JsonObjects with label, value, and
	 * properties
	 */
	public static <T> JsonArray of(List<T> objects, Function<T, String> label, Function<T, Object> value,
								   Function<T, JsonObject> properties)
	{
		return objects.stream().map(e -> JsonObject.of(e, label, value, properties))
				.collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray from an array of JsonElements. Null elements are
	 * converted to JsonNull.INSTANCE.
	 *
	 * @param values the array of JsonElements
	 * @return a JsonArray containing the specified elements
	 */
	public static JsonArray of(JsonElement... values)
	{
		return Stream.of(values).map(e -> e != null ? e : JsonNull.INSTANCE)
				.collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray from a Stream by converting each object to its
	 * text representation.
	 *
	 * @param stream the stream of objects to format
	 * @return a JsonArray containing the text representations
	 */
	public static JsonArray format(Stream<?> stream)
	{
		return stream.map(JsonElement::format).collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray from a Collection by converting each object to
	 * its text representation.
	 *
	 * @param objects the collection of objects to format
	 * @return a JsonArray containing the text representations
	 */
	public static JsonArray format(Collection<?> objects)
	{
		return format(objects.stream());
	}

	/**
	 * Creates a JsonArray from an array by converting each object to its
	 * text representation.
	 *
	 * @param objects the array of objects to format
	 * @return a JsonArray containing the text representations
	 */
	public static JsonArray format(Object... objects)
	{
		return format(Stream.of(objects));
	}

	/**
	 * Creates a JsonArray of formatted JsonObjects with label and value
	 * properties from a List.
	 *
	 * @param <T>     the type of objects in the list
	 * @param objects the list of objects to format
	 * @param label   function to extract the label from each object
	 * @param value   function to extract the value from each object
	 * @return a JsonArray containing formatted JsonObjects
	 */
	public static <T> JsonArray format(List<T> objects, Function<T, String> label, Function<T, Object> value)
	{
		return objects.stream().map(e -> JsonObject.format(e, label, value))
				.collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Parses a JSON string into a JsonArray. This is an alias for
	 * {@link #parse(String)}.
	 *
	 * @param string the JSON formatted string to parse
	 * @return a JsonArray representing the parsed JSON
	 */
	public static JsonArray valueOf(String string)
	{
		return parse(string);
	}
}