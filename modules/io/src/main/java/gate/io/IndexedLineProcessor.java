package gate.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.BiConsumer;

public class IndexedLineProcessor implements Processor<String>
{

	protected final String charset;
	protected final BiConsumer<Long, String> consumer;

	public IndexedLineProcessor(String charset, BiConsumer<Long, String> consumer)
	{
		this.charset = charset;
		this.consumer = consumer;
	}

	public IndexedLineProcessor(BiConsumer<Long, String> consumer)
	{
		this.charset = "utf-8";
		this.consumer = consumer;
	}

	@Override
	public String getCharset()
	{
		return charset;
	}

	@Override
	public long process(InputStream is) throws IOException
	{
		long index = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			consumer.accept(index++, line);
		return index + 1;

	}

	public static long process(File file, String charset, BiConsumer<Long, String> consumer) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return new IndexedLineProcessor(charset, consumer).process(is);
		}
	}

	public static long process(File file, BiConsumer<Long, String> consumer) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return new IndexedLineProcessor(consumer).process(is);
		}
	}

	public static long process(URL url, String charset, BiConsumer<Long, String> consumer) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return new IndexedLineProcessor(charset, consumer).process(is);
		}
	}
}
