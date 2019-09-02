package gate.type;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Vetoed;

@Vetoed
public class TempFileManager
{

	private final List<TempFile> list = new ArrayList<>();

	public synchronized TempFile create()
	{
		try
		{
			File file = File.createTempFile("Temp", ".tmp");
			file.deleteOnExit();
			TempFile tempFile = new TempFile(file);
			list.add(tempFile);
			return tempFile;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public synchronized void close()
	{
		list.forEach(e -> e.close());
	}
}
