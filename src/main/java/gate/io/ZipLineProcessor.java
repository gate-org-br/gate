package gate.io;

import gate.stream.CheckedPredicate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipLineProcessor extends AbstractProcessor<String>
{

	public ZipLineProcessor(String charset, CheckedPredicate<String> action)
	{
		super(charset, action);
	}

	@Override
	public long process(InputStream is) throws IOException, InvocationTargetException
	{
		try (ZipInputStream stream = new ZipInputStream(is);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset)))
		{
			long counter = 0;
			for (ZipEntry entry = stream.getNextEntry();
				entry != null;
				entry = stream.getNextEntry())
				if (!entry.isDirectory())
					for (String line = reader.readLine();
						line != null;
						line = reader.readLine())
					{
						try
						{
							if (!action.test(line))
								return counter;
						} catch (Exception ex)
						{
							throw new InvocationTargetException(ex);
						}
						counter++;
					}
			return counter;
		}

	}
}
