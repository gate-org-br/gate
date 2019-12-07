package gate.io;

import gate.converter.Converter;
import gate.error.ConversionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ObjectReader<T> implements Reader<Optional<T>>
{

	private final Class<T> type;
	private static final ConcurrentMap<Class<?>, ObjectReader<?>> INSTANCES
			= new ConcurrentHashMap<>();

	private ObjectReader(Class<T> type)
	{
		this.type = type;
	}

	public static <T> ObjectReader<T> getInstance(Class<T> type)
	{
		return (ObjectReader<T>) INSTANCES.computeIfAbsent(type, ObjectReader::new);
	}

	@Override
	public Optional<T> read(InputStream is) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try (StringWriter writer = new StringWriter())
		{
			for (int c = reader.read(); c != -1; c = reader.read())
				writer.write((char) c);
			writer.flush();
			return Optional.ofNullable((T) Converter
					.getConverter(type)
					.ofString(type, writer.toString()));
		} catch (ConversionException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getCharset()
	{
		return "UTF-8";
	}

}
