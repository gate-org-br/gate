package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.TempFileConverter;
import gate.handler.TempFileHandler;
import jakarta.servlet.http.Part;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A temporary file to be removed after processing.
 *
 * <p>
 * Instances of this class represent files created in the operating system
 * temporary directory and must be explicitly closed when no longer needed.
 * </p>
 *
 * <p>
 * This class implements {@link AutoCloseable}. Failing to invoke
 * {@link #close()} will cause the underlying temporary file to remain on disk,
 * resulting in a resource leak.
 * </p>
 *
 * <p>
 * When used inside the Gate execution pipeline, temporary files are
 * automatically cleaned up at the end of the request or asynchronous execution.
 * However, when created or used manually, the caller is fully responsible for
 * closing the instance.
 * </p>
 *
 * <p>
 * Recommended usage:
 * </p>
 *
 * <pre>{@code
 * try (TempFile file = TempFile.of(bytes)) {
 *     // use file
 * }
 * }</pre>
 *
 * <p>
 * Not closing this resource may lead to:
 * </p>
 * <ul>
 * <li>temporary files accumulating on disk</li>
 * <li>file descriptor leaks</li>
 * <li>unexpected behavior in long-running applications</li>
 * </ul>
 */
@Handler(TempFileHandler.class)
@Converter(TempFileConverter.class)
public class TempFile implements AutoCloseable
{

	private final File file;
	private InputStream inputStream;
	private OutputStream outputStream;
	private static final ThreadLocal<List<TempFile>> FILES = new ThreadLocal<>();

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
	 * @throws java.io.IOException if an IOException occurs when trying to
	 * open the stream
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
	 * @throws java.io.IOException if an IOException occurs when trying to
	 * open the stream
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
	 * @throws java.io.IOException if an IOException occurs when trying to
	 * read the bytes
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
		if (FILES.get() == null)
			FILES.set(new ArrayList<>());
		try
		{
			File file = File.createTempFile("temp", ".tmp");
			TempFile tempFile = new TempFile(file);
			FILES.get().add(tempFile);
			return tempFile;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Removes and deletes all temporary files created in the current thread
	 * context.
	 *
	 * <p>
	 * This method closes every {@link TempFile} registered in the current
	 * {@link ThreadLocal} and deletes the underlying temporary files.
	 * </p>
	 *
	 * <p>
	 * It is intended to be called at the end of a processing cycle, such
	 * as:
	 * </p>
	 * <ul>
	 * <li>when an HTTP request finishes</li>
	 * <li>when an asynchronous task completes</li>
	 * </ul>
	 *
	 * <p>
	 * The cleanup is always limited to the current thread. Temporary files
	 * created in other threads are not affected.
	 * </p>
	 *
	 * <p>
	 * This method is safe to call multiple times. If no temporary files
	 * were created in the current thread, the method does nothing.
	 * </p>
	 *
	 * <p>
	 * After execution, the internal {@link ThreadLocal} context is cleared
	 * to prevent resource leaks in thread pools.
	 * </p>
	 */
	public static void cleanup()
	{
		var files = FILES.get();

		if (files == null)
			return;

		try
		{
			for (var file : files)
				file.close();
		} finally
		{
			FILES.remove();
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
		{
			try
			{
				outputStream.close();
			} catch (IOException | RuntimeException ex)
			{
				LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
			}
		}

		if (inputStream != null)
		{
			try
			{
				inputStream.close();
			} catch (IOException | RuntimeException ex)
			{
				LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
			}
		}

		try
		{
			Files.deleteIfExists(file.toPath());
		} catch (IOException | RuntimeException ex)
		{
			LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
		}
	}

	@Override
	public String toString()
	{
		return getName();
	}

}