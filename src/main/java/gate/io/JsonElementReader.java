package gate.io;

import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JsonElementReader extends AbstractReader<Optional<JsonElement>>
{

	private static final JsonElementReader INSTANCE = new JsonElementReader();
	private static final ConcurrentMap<String, JsonElementReader> INSTANCES
		= new ConcurrentHashMap<String, JsonElementReader>();

	private JsonElementReader()
	{
	}

	private JsonElementReader(String charset)
	{
		super(charset);
	}

	@Override
	public Optional<JsonElement> read(InputStream is) throws IOException
	{
		try
		{
			return new JsonParser(new BufferedReader(new InputStreamReader(is)))
				.stream().findAny();
		} catch (ConversionException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public static JsonElementReader getInstance()
	{
		return INSTANCE;
	}

	public static JsonElementReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, JsonElementReader::new);
	}

	public static Optional<JsonElement> read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return JsonElementReader.getInstance().read(is);
		}
	}

	public static Optional<JsonElement> read(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return JsonElementReader.getInstance(charset).read(is);
		}
	}

	public static Optional<JsonElement> read(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return JsonElementReader.getInstance().read(is);
		}
	}

	public static Optional<JsonElement> read(String charset, URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return JsonElementReader.getInstance(charset).read(is);
		}
	}

}
