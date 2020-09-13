package gate.converter;

import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.util.Reflection;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionConverter extends ObjectConverter
{

	@Override
	public String toText(Class<?> type, Object object)
	{
		StringBuilder string = new StringBuilder();
		for (Object obj : ((Iterable<?>) object))
		{
			if (obj != null)
			{
				if (string.length() > 0)
					string.append(", ");

				string.append(Converter.toText(obj));
			}
		}
		return string.toString();
	}

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		writer.write(JsonToken.Type.OPEN_ARRAY, null);

		boolean first = true;
		for (Object element : ((Iterable<?>) object))
		{
			if (first)
				first = false;
			else
				writer.write(JsonToken.Type.COMMA, null);

			if (element != null)
			{
				Converter converter = Converter.getConverter(element.getClass());
				converter.toJson(writer, (Class<Object>) element.getClass(), element);
			} else
				writer.write(JsonToken.Type.NULL, null);
		}

		writer.write(JsonToken.Type.CLOSE_ARRAY, null);
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a collection");

		Collection collection = new ArrayList<>();
		Converter converter = Converter.getConverter(Reflection.getRawType(elementType));

		do
		{
			scanner.scan();
			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				collection.add(converter.ofJson(scanner, elementType,
					Reflection.getElementType(elementType)));
			else if (!collection.isEmpty())
				throw new ConversionException(scanner.getCurrent() + " is not a collection");
		} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a collection");

		scanner.scan();
		return collection;

	}

	@Override
	public <T> void toJsonText(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		writer.write(JsonToken.Type.OPEN_ARRAY, null);

		boolean first = true;
		for (Object element : ((Iterable<?>) object))
		{
			if (first)
				first = false;
			else
				writer.write(JsonToken.Type.COMMA, null);

			if (element != null)
			{
				Converter converter = Converter.getConverter(element.getClass());
				converter.toJsonText(writer, (Class<Object>) element.getClass(), element);
			} else
				writer.write(JsonToken.Type.NULL, null);
		}

		writer.write(JsonToken.Type.CLOSE_ARRAY, null);
	}
}
