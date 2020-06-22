package gate.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StringReader implements Reader<String>
{

	private static final StringReader INSTANCE = new StringReader();
	private static final ConcurrentMap<String, StringReader> INSTANCES
		= new ConcurrentHashMap<String, StringReader>();

	private final String charset;

	private StringReader(String charset)
	{
		this.charset = charset;
	}

	private StringReader()
	{
		this(Charset.defaultCharset().name());
	}

	public static StringReader getInstance()
	{
		return INSTANCE;
	}

	public static StringReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, StringReader::new);
	}

	@Override
	public String read(InputStream is) throws IOException
	{
		BufferedReader reader
			= new BufferedReader(new InputStreamReader(is, charset));
		try (StringWriter writer = new StringWriter())
		{
			for (int c = reader.read(); c != -1; c = reader.read())
				writer.write((char) c);
			writer.flush();
			return writer.toString();
		}
	}

	@Override
	public String getCharset()
	{
		return charset;
	}

	public static String read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return INSTANCE.read(is);
		}
	}

	public static String read(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return getInstance(charset).read(is);
		}
	}

	public static String read(java.net.URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return INSTANCE.read(is);
		}
	}

	public static String read(String charset, java.net.URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return INSTANCE.read(is);

		}
	}
}
