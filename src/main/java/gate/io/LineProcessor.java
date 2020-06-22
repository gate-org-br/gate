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
	public void process(InputStream is) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			consumer.accept(line);

	}

	public static void process(File file, String charset, Consumer<String> consumer) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			new LineProcessor(charset, consumer).process(is);
		}
	}

	public static void process(File file, Consumer<String> consumer) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			new LineProcessor(consumer).process(is);
		}
	}

	public static void process(URL url, String charset, Consumer<String> consumer) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			new LineProcessor(charset, consumer).process(is);
		}
	}
}
