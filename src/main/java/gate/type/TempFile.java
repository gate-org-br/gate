package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.TempFileConverter;
import gate.handler.TempFileHandler;
import gate.io.IOStreamTransferer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.servlet.http.Part;

/**
 * A temporary file to be removed after processing.
 */
@Handler(TempFileHandler.class)
@Converter(TempFileConverter.class)
public class TempFile implements AutoCloseable
{

	private final File file;

	TempFile(File file)
	{
		this.file = file;
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
			return new FileInputStream(file);
		} catch (FileNotFoundException ex)
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
			return new FileOutputStream(file);
		} catch (FileNotFoundException ex)
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
	 * @return a NamedTempFile describing this temporary file and the given name
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
		try
		{
			File file = File.createTempFile("Temp", ".tmp");
			file.deleteOnExit();
			return new TempFile(file);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
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
		TempFile tempFile = TempFile.empty();

		try (InputStream inputStream = part.getInputStream();
			OutputStream outputStream = tempFile.getOutputStream())
		{
			IOStreamTransferer.transfer(inputStream, outputStream);
			return tempFile;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Creates a new temporary file with the contents of the specified InputStream.
	 *
	 * @param inputStream the InputStream object from where to get the temporary file data
	 *
	 * @return the temporary file created
	 */
	public static TempFile of(InputStream inputStream)
	{
		TempFile tempFile = TempFile.empty();

		try (OutputStream outputStream = tempFile.getOutputStream())
		{
			IOStreamTransferer.transfer(inputStream, outputStream);
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
		file.delete();
	}
}
