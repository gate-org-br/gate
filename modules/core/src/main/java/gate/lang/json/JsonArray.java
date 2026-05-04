package gate.lang.json;

import gate.error.ConversionException;
import gate.util.Reflection;

import java.io.Serial;
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
public class JsonArray implements List<JsonElement>, JsonCollection
{

	private final List<JsonElement> values = new ArrayList<>();

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty JsonArray.
	 */
	public JsonArray() {}

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
	public String toString() {return JsonElement.stringify(this);}

	/**
	 * Converts this JsonArray to a Java object of the specified type.
	 * <p>
	 * This method cannot be used without specifying an element type. Use
	 * {@link #decode(java.lang.reflect.Type, java.lang.reflect.Type)}
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
	public <T> T decode(Class<T> type) {throw new UnsupportedOperationException("Can't create java object from json array without element type");}

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
	public <T> T decode(java.lang.reflect.Type type, java.lang.reflect.Type elementType)
	{
		Class<T> clazz = (Class<T>) type;
		Class<?> elementClazz = Reflection.getRawType(elementType);

		return clazz.isAssignableFrom(Set.class)
				? (T) stream()
					  .map(e -> e.decode(elementClazz, Reflection.getElementGenericType(elementType)))
					  .collect(Collectors.toSet())
				: (T) stream()
					  .map(e -> e.decode(elementClazz, Reflection.getElementGenericType(elementType))).toList();
	}

	/**
	 * Returns the natural Java representation of this JSON array.
	 * <p>
	 * Each element is recursively converted through
	 * {@link JsonElement#unwrap()}.
	 *
	 * @return a {@link List} containing the natural Java representation of
	 * each JSON element
	 */
	@Override public List<Object> unwrap() {return stream().map(JsonElement::unwrap).toList();}

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

	@Override
	public int size()
	{
		return values.size();
	}

	@Override
	public JsonElement path(int index)
	{
		int position = index < 0 ? size() + index : index;
		return position >= 0 && position < size() ? get(position) : JsonNull.INSTANCE;
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
		return new HashSet<>(values).containsAll(c);
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
	 * Inserts a JsonElement at the end of this array and returns this array.
	 *
	 * @param value the JsonElement to insert
	 * @return this JsonArray for method chaining
	 */
	public JsonArray insert(JsonElement value)
	{
		add(value);
		return this;
	}

	/**
	 * Inserts a JsonElement at the specified index and returns this array.
	 *
	 * @param index the insertion index
	 * @param value the JsonElement to insert
	 * @return this JsonArray for method chaining
	 */
	public JsonArray insert(int index, JsonElement value)
	{
		add(index, value);
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
		return index >= 0 && values.size() > index && values.get(index) instanceof JsonString string ? Optional.of(string.unwrap())
				: Optional.empty();
	}

	/**
	 * Resolves an array element by index, where the index is provided as a
	 * string.
	 * <p>
	 * If the specified segment is not a valid non-negative integer or points
	 * outside the array bounds, this method returns {@link JsonNull#INSTANCE}.
	 * /**
	 * Gets a JsonElement at the specified index.
	 *
	 * @param index the index
	 * @return an Optional containing the JsonElement, or empty if index out
	 * of bounds
	 */
	public Optional<JsonElement> getJsonElement(int index)
	{
		return index >= 0 && values.size() > index ? Optional.ofNullable(values.get(index)) : Optional.empty();
	}

	/**
	 * Creates a JsonArray from a Stream of objects by converting each to a
	 * JsonElement.
	 *
	 * @param stream the stream of objects to convert
	 * @return a JsonArray containing the converted elements
	 */
	public static JsonArray wrap(Stream<?> stream)
	{
		return stream.map(JsonElement::wrap).collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray from a Collection of objects by converting each
	 * to a JsonElement.
	 *
	 * @param objects the collection of objects to convert
	 * @return a JsonArray containing the converted elements
	 */
	public static JsonArray wrap(Collection<?> objects)
	{
		return JsonArray.wrap(objects.stream());
	}

	/**
	 * Creates a JsonArray from an array of objects by converting each to a
	 * JsonElement.
	 *
	 * @param objects the array of objects to convert
	 * @return a JsonArray containing the converted elements
	 */
	public static JsonArray wrap(Object... objects)
	{
		return JsonArray.wrap(Stream.of(objects));
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
	public static <T> JsonArray entries(List<T> objects, Function<T, String> label, Function<T, Object> value)
	{
		return objects.stream().map(e -> JsonObject.entries(e, label, value))
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
	public static <T> JsonArray entries(List<T> objects, Function<T, String> label, Function<T, Object> value,
	                                    Function<T, JsonObject> properties)
	{
		return objects.stream().map(e -> JsonObject.entries(e, label, value, properties))
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
	public static JsonArray render(Stream<?> stream)
	{
		return stream.map(JsonElement::render).collect(Collectors.toCollection(JsonArray::new));
	}

	/**
	 * Creates a JsonArray from a Collection by converting each object to
	 * its text representation.
	 *
	 * @param objects the collection of objects to format
	 * @return a JsonArray containing the text representations
	 */
	public static JsonArray render(Collection<?> objects) {return render(objects.stream());}

	/**
	 * Creates a JsonArray from an array by converting each object to its
	 * text representation.
	 *
	 * @param objects the array of objects to format
	 * @return a JsonArray containing the text representations
	 */
	public static JsonArray render(Object... objects) {return render(Stream.of(objects));}

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
	public static <T> JsonArray render(List<T> objects, Function<T, String> label, Function<T, Object> value)
	{
		return objects.stream().map(e -> JsonObject.render(e, label, value))
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