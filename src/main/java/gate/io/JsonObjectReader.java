package gate.io;

import gate.error.ConversionException;
import gate.lang.json.JsonObject;
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

public class JsonObjectReader extends AbstractReader<Optional<JsonObject>>
{

	private static final JsonObjectReader INSTANCE = new JsonObjectReader();
	private static final ConcurrentMap<String, JsonObjectReader> INSTANCES = new ConcurrentHashMap<>();

	private JsonObjectReader()
	{
	}

	private JsonObjectReader(String charset)
	{
		super(charset);
	}

	@Override
	public Optional<JsonObject> read(InputStream is) throws IOException
	{
		try
		{
			return new JsonParser(new BufferedReader(new InputStreamReader(is)))
				.stream()
				.map(e -> (JsonObject) e)
				.findAny();
		} catch (ConversionException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public static JsonObjectReader getInstance()
	{
		return INSTANCE;
	}

	public static JsonObjectReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, JsonObjectReader::new);
	}

	public static Optional<JsonObject> read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return JsonObjectReader.getInstance().read(is);
		}
	}

	public static Optional<JsonObject> read(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return JsonObjectReader.getInstance(charset).read(is);
		}
	}

	public static Optional<JsonObject> read(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return JsonObjectReader.getInstance().read(is);
		}
	}

	public static Optional<JsonObject> read(String charset, URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return JsonObjectReader.getInstance(charset).read(is);
		}
	}

}
