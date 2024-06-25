package gate.io;

import gate.converter.Converter;
import java.io.IOException;
import java.io.InputStream;
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
		return new ObjectReader("application/json", type, null);
	}

	public static <T> ObjectReader<T> getInstance(String contentType, Class<T> type)
	{
		return new ObjectReader(contentType, type, null);
	}

	public static <T> ObjectReader<T> getInstance(Class<T> type, Class<?> elementType)
	{
		return new ObjectReader("application/json", type, elementType);
	}

	public static <T> ObjectReader<T> getInstance(String contentType, Class<T> type, Class<?> elementType)
	{
		return new ObjectReader(contentType, type, elementType);
	}

	@Override
	public Optional<T> read(InputStream is) throws IOException
	{
		String string = StringReader.getInstance().read(is);
		if ("application/json".equalsIgnoreCase(contentType))
			return Optional.ofNullable((T) Converter.fromJson(type, elementType, string));
		return Optional.ofNullable((T) Converter.fromString(type, string));
	}

	@Override
	public String getCharset()
	{
		return "UTF-8";
	}
}
