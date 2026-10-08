package gate.type;

import java.io.File;

/**
 * A named temporary file to be removed after processing.
 */
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

	public static NamedTempFile of(String name, File file)
	{
		return new NamedTempFile(file, name);
	}

}
