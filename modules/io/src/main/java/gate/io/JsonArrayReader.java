package gate.io;

import gate.error.ConversionException;
import gate.lang.json.JsonArray;
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

public class JsonArrayReader extends AbstractReader<Optional<JsonArray>>
{

	private static final JsonArrayReader INSTANCE = new JsonArrayReader();
	private static final ConcurrentMap<String, JsonArrayReader> INSTANCES =
			new ConcurrentHashMap<>();

	private JsonArrayReader()
	{}

	private JsonArrayReader(String charset)
	{
		super(charset);
	}

	@Override
	@SuppressWarnings("resource")
	public Optional<JsonArray> read(InputStream is) throws IOException
	{
		try
		{
			return new JsonParser(new BufferedReader(new InputStreamReader(is))).stream()
					.map(e -> (JsonArray) e).findAny();
		} catch (ConversionException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public static JsonArrayReader getInstance()
	{
		return INSTANCE;
	}

	public static JsonArrayReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, JsonArrayReader::new);
	}

	public static Optional<JsonArray> read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return JsonArrayReader.getInstance().read(is);
		}
	}

	public static Optional<JsonArray> read(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return JsonArrayReader.getInstance(charset).read(is);
		}
	}

	public static Optional<JsonArray> read(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return JsonArrayReader.getInstance().read(is);
		}
	}

	public static Optional<JsonArray> read(String charset, URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return JsonArrayReader.getInstance(charset).read(is);
		}
	}

}
