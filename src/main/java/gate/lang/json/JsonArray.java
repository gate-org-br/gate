package gate.lang.json;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.JsonElementConverter;
import gate.error.ConversionException;
import gate.handler.JsonElementHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Represents a JSON array as a List of JsonElement.
 *
 * @author Davi Nunes da Silva
 */
@Handler(JsonElementHandler.class)
@Converter(JsonElementConverter.class)
public class JsonArray implements List<JsonElement>, JsonElement
{

	private final List<JsonElement> values = new ArrayList<>();

	private static final long serialVersionUID = 1L;

	public JsonArray()
	{
	}

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
		return o instanceof JsonArray
			&& values.equals(((JsonArray) o).values);
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
	 * Parses a JSON formatted string into a JsonArray object.
	 *
	 * @param json the JSON formatted string to be parsed into a JsonArray
	 * object
	 *
	 * @return a JsonArray object representing the JSON formatted string
	 * specified
	 *
	 * @throws ConversionException if an error occurs while trying to parse
	 * the specified JSON formatted string
	 * @throws NullPointerException if any of the parameters is null
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
	 *
	 * @param jsonArray the JsonArray object to be formatted on JSON
	 * notation
	 *
	 * @return a JSON formatted string representing the specified JsonArray
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	public static String format(JsonArray jsonArray)
	{
		Objects.requireNonNull(jsonArray);
		return JsonElement.format(jsonArray);
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
}
