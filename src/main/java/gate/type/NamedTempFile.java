package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.TempFileConverter;
import gate.handler.NamedTempFileHandler;
import java.io.File;

/**
 * A named temporary file to be removed after processing.
 */
@Converter(TempFileConverter.class)
@Handler(NamedTempFileHandler.class)
public class NamedTempFile extends TempFile
{

	private final String name;

	NamedTempFile(File file, String name)
	{
		super(file);
		this.name = name;
	}

	/**
	 * Returns the name of the temporary file.
	 *
	 * @return the name of the temporary file
	 */
	@Override
	public String getName()
	{
		return name;
	}

}
