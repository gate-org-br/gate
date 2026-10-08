package gate.io;

import gate.adapter.converter.Converter;
import gate.lang.json.JsonElement;
import gate.util.Reflection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Optional;

public class ObjectReader<T> implements Reader<Optional<T>>
{

	private final Class<T> type;
	private final Class<?> elementType;
	private final String contentType;

	private ObjectReader(String contentType, Class<T> type, Class<?> elementType)
	{
		this.contentType = contentType;
		this.type = type;
		this.elementType = elementType;
	}

	public static <T> ObjectReader<T> getInstance(Class<T> type)
	{
		return new ObjectReader<T>("application/json", type, null);
	}

	public static <T> ObjectReader<T> getInstance(String contentType, Class<T> type)
	{
		return new ObjectReader<T>(contentType, type, null);
	}

	public static <T> ObjectReader<T> getInstance(Class<T> type, Class<?> elementType)
	{
		return new ObjectReader<T>("application/json", type, elementType);
	}

	public static <T> ObjectReader<T> getInstance(String contentType, Class<T> type, Class<?> elementType)
	{
		return new ObjectReader<T>(contentType, type, elementType);
	}

	@Override
	public Optional<T> read(InputStream is) throws IOException
	{
		String string = StringReader.getInstance().read(is);
		if ("application/json".equalsIgnoreCase(contentType))
			return Optional.ofNullable(JsonElement.parse(string).decode(getGenericType()));
		return Optional.ofNullable(Converter.fromString(type, string));
	}

	private Type getGenericType()
	{
		if (elementType == null)
			return type;

		return Reflection.parameterizedType(type, elementType);
	}

	@Override
	public String getCharset()
	{
		return "UTF-8";
	}
}
