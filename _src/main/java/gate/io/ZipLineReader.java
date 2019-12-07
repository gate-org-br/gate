package gate.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipLineReader implements Reader<List<String>>
{

	private static final ZipLineReader INSTANCE
		= new ZipLineReader();

	private static final ConcurrentMap<String, ZipLineReader> INSTANCES
		= new ConcurrentHashMap<String, ZipLineReader>();

	private final String charset;

	private ZipLineReader(String charset)
	{
		this.charset = charset;
	}

	private ZipLineReader()
	{
		this(Charset.defaultCharset().name());
	}

	public static ZipLineReader getInstance()
	{
		return INSTANCE;
	}

	public static ZipLineReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, ZipLineReader::new);
	}

	@Override
	public List<String> read(InputStream is) throws IOException
	{
		try (ZipInputStream stream = new ZipInputStream(is);
			BufferedReader reader
			= new BufferedReader(new InputStreamReader(stream, charset)))
		{
			List<String> lines = new ArrayList<>();
			for (ZipEntry entry = stream.getNextEntry();
				entry != null;
				entry = stream.getNextEntry())
				if (!entry.isDirectory())
					reader.lines().forEach(lines::add);
			return lines;
		}
	}

	@Override
	public String getCharset()
	{
		return charset;
	}

}
