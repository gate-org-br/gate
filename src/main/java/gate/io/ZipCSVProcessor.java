package gate.io;

import gate.lang.csv.CSVParser;
import gate.stream.CheckedPredicate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipCSVProcessor extends AbstractProcessor<List<String>>
{

	public ZipCSVProcessor(String charset, CheckedPredicate<List<String>> consumer)
	{
		super(charset, consumer);
	}

	public ZipCSVProcessor(CheckedPredicate<List<String>> consumer)
	{
		super(consumer);
	}

	@Override
	public long process(InputStream is) throws IOException, InvocationTargetException
	{
		try (ZipInputStream stream = new ZipInputStream(is);
			CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(is, getCharset()))))
		{
			for (ZipEntry entry = stream.getNextEntry();
				entry != null;
				entry = stream.getNextEntry())
			{
				if (!entry.isDirectory())
					for (List<String> line : parser)
					{
						try
						{
							if (!action.test(line))
								return parser.getLineNumber() + 1;
						} catch (Exception ex)
						{
							throw new InvocationTargetException(ex);
						}
					}
			}

			return (int) parser.getLineNumber() + 1;
		}

	}
}
