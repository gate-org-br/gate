package gate.io;

import gate.function.TryPredicate;
import gate.lang.csv.CSVParser;
import gate.lang.csv.Row;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipCSVProcessor extends AbstractProcessor<List<String>>
{

	public ZipCSVProcessor(String charset, TryPredicate<List<String>> consumer)
	{
		super(charset, consumer);
	}

	public ZipCSVProcessor(TryPredicate<List<String>> consumer)
	{
		super(consumer);
	}

	@Override
	public long process(InputStream is) throws IOException, InvocationTargetException
	{
		try (ZipInputStream stream = new ZipInputStream(is);
			 CSVParser parser = CSVParser.of(is, Charset.forName(getCharset())))
		{
			long count = 0;
			for (ZipEntry entry = stream.getNextEntry();
				 entry != null;
				 entry = stream.getNextEntry())
			{
				if (!entry.isDirectory())
					for (Row line : parser)
					{
						try
						{
							count++;
							if (!action.test(line))
								return line.getIndex() + 1;
						} catch (Exception ex)
						{
							throw new InvocationTargetException(ex);
						}
					}
			}

			return count;
		}

	}
}
