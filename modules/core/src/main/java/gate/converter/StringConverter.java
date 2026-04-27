package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class StringConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		if (string != null)
			string = string.trim();
		return string == null || string.isEmpty() ? null : string;
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case NULL:
				scanner.scan();
				return null;
			case STRING:
				String value = scanner.getCurrent().toString();
				scanner.scan();
				return value;
			default:
				throw new ConversionException(scanner.getCurrent() + " is not a string");
		}
	}

	/**
	 * Serializes the specified {@link java.lang.String} on JSON notation.
	 * <p>
	 * A non null java String will be formatted as a double quoted JSON string. A null reference will be formatted
	 * as a JSON Null.
	 *
	 * @throws gate.error.ConversionException if the specified object is not a string
	 */
	@Override
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object == null)
			writer.write(JsonToken.Type.NULL, null);
		else if (object instanceof String)
			writer.write(JsonToken.Type.STRING, (String) object);
		else
			throw new ConversionException(object.getClass().getName() + " is not a String");
	}
}