package gate.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LineReader extends AbstractReader<List<String>>
{

	private static final LineReader INSTANCE
			= new LineReader();

	private static final ConcurrentMap<String, LineReader> INSTANCES
			= new ConcurrentHashMap<String, LineReader>();

	private LineReader()
	{
	}

	private LineReader(String charset)
	{
		super(charset);
	}

	@Override
	public List<String> read(InputStream is) throws IOException
	{
		BufferedReader reader
				= new BufferedReader(new InputStreamReader(is, charset));
		List<String> lines = new ArrayList<>();
		for (String line = reader.readLine();
				line != null;
				line = reader.readLine())
			lines.add(line);
		return lines;
	}

	public static LineReader getInstance()
	{
		return INSTANCE;
	}

	public static LineReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, LineReader::new);
	}

	public static List<String> read(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return LineReader.getInstance().read(is);
		}
	}

	public static List<String> read(String charset, byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return LineReader.getInstance(charset).read(is);
		}
	}

	public static List<String> read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return LineReader.getInstance().read(is);
		}
	}

	public static List<String> read(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return LineReader.getInstance(charset).read(is);
		}
	}

	public static List<String> read(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return LineReader.getInstance().read(is);
		}
	}

	public static List<String> read(String charset, URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return LineReader.getInstance(charset).read(is);

		}
	}
}
