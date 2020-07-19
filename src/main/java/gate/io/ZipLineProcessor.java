package gate.io;

import gate.util.Counter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipLineProcessor extends AbstractProcessor<String>
{

	public ZipLineProcessor(String charset, Consumer<String> consumer)
	{
		super(charset, consumer);
	}

	public ZipLineProcessor(Consumer<String> consumer)
	{
		super(consumer);
	}

	@Override
	public long process(InputStream is) throws IOException
	{
		try (ZipInputStream stream = new ZipInputStream(is);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset)))
		{
			Counter counter = new Counter();
			for (ZipEntry entry = stream.getNextEntry();
				entry != null;
				entry = stream.getNextEntry())
				if (!entry.isDirectory())
					reader.lines().forEach(e ->
					{
						consumer.accept(e);
						counter.inc();
					});
			return counter.value();
		}

	}
}
