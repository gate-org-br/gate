package gate.type;

import gate.annotation.Handler;
import gate.handler.TempFileHandler;
import java.io.File;
import java.io.IOException;

@Handler(TempFileHandler.class)
public class TempFile
{

	private final File file;

	TempFile(File file)
	{
		this.file = file;

	}

	public File getFile()
	{
		return file;
	}

	public void delete()
	{
		file.delete();
	}

	public static TempFile newInstance() throws IOException
	{
		File file = File.createTempFile("Temp", ".zip");
		file.deleteOnExit();
		return new TempFile(file);
	}
}
