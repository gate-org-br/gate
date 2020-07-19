package gate.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

public class LineProcessor extends AbstractProcessor<String>
{

	public LineProcessor(String charset, Consumer<String> consumer)
	{
		super(charset, consumer);
	}

	public LineProcessor(Consumer<String> consumer)
	{
		super(consumer);
	}

	@Override
	public long process(InputStream is) throws IOException
	{
		int count = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			consumer.accept(line);
			count++;
		}
		return count;

	}

	public static long process(File file, String charset, Consumer<String> consumer) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return new LineProcessor(charset, consumer).process(is);
		}
	}

	public static long process(File file, Consumer<String> consumer) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return new LineProcessor(consumer).process(is);
		}
	}

	public static long process(URL url, String charset, Consumer<String> consumer) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return new LineProcessor(charset, consumer).process(is);
		}
	}
}
