package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.TempFileConverter;
import gate.handler.TempFileHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.Part;
import org.slf4j.LoggerFactory;

/**
 * A temporary file to be removed after processing.
 */
@Handler(TempFileHandler.class)
@Converter(TempFileConverter.class)
public class TempFile implements AutoCloseable
{

	private final File file;
	private InputStream inputStream;
	private OutputStream outputStream;

	protected TempFile(File file)
	{
		this.file = file;
	}

	/**
	 * Returns the name of the temporary file.
	 *
	 * @return the name of the temporary file
	 */
	public String getName()
	{
		return file.getName();
	}

	/**
	 * Creates an input stream to read the temporary file data.
	 *
	 * @return an input stream to read the temporary file data
	 */
	public InputStream getInputStream()
	{
		try
		{
			if (inputStream != null)
				inputStream.close();
			inputStream = new FileInputStream(file);
			return inputStream;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Creates an output stream to write data into the temporary file.
	 *
	 * @return an output stream to write data into the temporary file.
	 */
	public OutputStream getOutputStream()
	{
		try
		{
			if (outputStream != null)
				outputStream.close();
			outputStream = new FileOutputStream(file);
			return outputStream;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Return the length of the temporary file.
	 *
	 * @return the length of the temporary file
	 */
	public long length()
	{
		return file.length();
	}

	/**
	 * Tests whether the temporary file exists.
	 *
	 * @return true if and only if the temporary file exists
	 */
	public boolean exists()
	{
		return file.exists();
	}

	/**
	 * Applies a name to the temporary file.
	 *
	 * @param name the name to be applied to the temporary file
	 *
	 * @return a NamedTempFile describing this temporary file and the given
	 * name
	 */
	public NamedTempFile named(String name)
	{
		return new NamedTempFile(file, name);
	}

	/**
	 * Creates a new empty temporary file.
	 *
	 * @return the empty temporary file created
	 */
	public static TempFile empty()
	{
		return CDI.current().select(TempFileManager.class).get().create();
	}

	/**
	 * Creates a new temporary file with the contents of the specified Part.
	 *
	 * @param part the Part object from where to get the temporary file data
	 *
	 * @return the temporary file created
	 */
	public static TempFile of(Part part)
	{
		TempFile tempFile = TempFile.empty()
			.named(part.getSubmittedFileName());

		try (InputStream inputStream = part.getInputStream();
			OutputStream outputStream = tempFile.getOutputStream())
		{
			inputStream.transferTo(outputStream);
			return tempFile;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Creates a new temporary file with the contents of the specified
	 * InputStream.
	 *
	 * @param inputStream the InputStream object from where to get the
	 * temporary file data
	 *
	 * @return the temporary file created
	 */
	public static TempFile of(InputStream inputStream)
	{
		TempFile tempFile = TempFile.empty();

		try (OutputStream outputStream = tempFile.getOutputStream())
		{
			inputStream.transferTo(outputStream);
			return tempFile;
		} catch (IOException ex)
		{
			tempFile.close();
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Free all associated IO streams and remove the temporary file.
	 */
	@Override
	public void close()
	{
		if (outputStream != null)
			try
		{
			outputStream.close();
		} catch (IOException ex)
		{
			LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
		}

		if (inputStream != null)
			try
		{
			inputStream.close();
		} catch (IOException ex)
		{
			LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
		}

		file.delete();
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
