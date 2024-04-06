package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.TempFileConverter;
import gate.handler.TempFileHandler;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
	 * @throws java.io.IOException if an IOException occurs when trying to open the stream
	 */
	public InputStream getInputStream() throws IOException
	{

		if (inputStream != null)
			inputStream.close();
		inputStream = new FileInputStream(file);
		return inputStream;

	}

	/**
	 * Creates an output stream to write data into the temporary file.
	 *
	 * @return an output stream to write data into the temporary file.
	 * @throws java.io.IOException if an IOException occurs when trying to open the stream
	 */
	public OutputStream getOutputStream() throws IOException
	{
		if (outputStream != null)
			outputStream.close();
		outputStream = new FileOutputStream(file);
		return outputStream;

	}

	/**
	 * Reads all the bytes from the file into a byte array
	 *
	 * @return a byte array containing the file bytes
	 * @throws java.io.IOException if an IOException occurs when trying to read the bytes
	 */
	public byte[] getBytes() throws IOException
	{
		return Files.readAllBytes(file.toPath());
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

	public Path getPath()
	{
		return file.toPath();
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
	 * Creates a new temporary file with the contents of a byte array.
	 *
	 * @param bytes a byte array with the temporary file data
	 *
	 * @return the temporary file created
	 */
	public static TempFile of(byte[] bytes)
	{
		TempFile tempFile = TempFile.empty();

		try (OutputStream outputStream = tempFile.getOutputStream())
		{
			outputStream.write(bytes);
			return tempFile;
		} catch (IOException ex)
		{
			tempFile.close();
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
