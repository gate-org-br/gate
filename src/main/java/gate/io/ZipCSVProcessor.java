package gate.io;

import gate.lang.csv.CSVParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipCSVProcessor extends AbstractProcessor<List<String>>
{

	public ZipCSVProcessor(String charset, Consumer<List<String>> consumer)
	{
		super(charset, consumer);
	}

	public ZipCSVProcessor(Consumer<List<String>> consumer)
	{
		super(consumer);
	}

	@Override
	public long process(InputStream is) throws IOException
	{
		try (ZipInputStream stream = new ZipInputStream(is);
			CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(is, getCharset()))))
		{
			for (ZipEntry entry = stream.getNextEntry();
				entry != null;
				entry = stream.getNextEntry())
			{
				if (!entry.isDirectory())
					parser.forEach(consumer);
			}

			return (int) parser.getLineNumber() + 1;
		}

	}
}
