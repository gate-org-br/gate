package gate.lang.csv;

import gate.error.AppError;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Appends lists and arrays of strings, as CSV formatted rows, into an {@link java.io.Writer}.
 * <p>
 * Each string list or array will be written as a CSV row having each column double quoted and separated by commas
 */
public class CSVFormatter implements AutoCloseable
{

	private final Writer writer;
	private final char separator, delimiter;

	private static final char SEPARATOR = ';';
	private static final char DELIMITER = '"';
	private static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * Constructs a new CSVFormatter for the specified Writer.
	 *
	 * @param writer the writer where CSV rows will be appended
	 * @param separator the column separator to be used when generating rows
	 * @param delimiter the column delimiter to be used when generating rows
	 */
	private CSVFormatter(Writer writer, char separator, char delimiter)
	{
		this.writer = writer;
		this.separator = separator;
		this.delimiter = delimiter;
	}

	/**
	 * Appends a string list to the writer as a new CSV line.
	 *
	 * @param values the list of strings to be appended to the previously specified writer
	 *
	 * @throws java.io.UncheckedIOException If an I/O error occurs
	 * @throws java.lang.NullPointerException If any of the parameters is null
	 */
	public void writeLine(List<String> values)
	{
		try
		{
			write(values);
			writer.write(System.lineSeparator());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	private void write(List<String> values)
	{
		try
		{
			Objects.requireNonNull(values);
			boolean firstVal = true;
			for (String val : values)
			{
				if (!firstVal)
					writer.write(separator);
				writer.write(delimiter);
				for (int i = 0; i < val.length(); i++)
				{
					char ch = val.charAt(i);
					if (ch == DELIMITER)
						writer.write("\\\"");
					else
						writer.write(ch);
				}
				writer.write(delimiter);
				firstVal = false;
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Appends a string array to the writer as a new CSV line.
	 *
	 * @param values the list of strings to be appended to the previously specified writer
	 *
	 * @throws java.io.UncheckedIOException If an I/O error occurs
	 * @throws java.lang.NullPointerException If any of the parameters is null
	 */
	public void writeLine(String... values)
	{
		CSVFormatter.this.writeLine(Arrays.asList(values));
	}

	@Override
	public void close()
	{
		try
		{
			writer.close();
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	public static String format(List<String> line)
	{
		try (StringWriter writer = new StringWriter();
			CSVFormatter formatter = CSVFormatter.of(writer))
		{
			formatter.write(line);
			return writer.toString();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public static String format(List<String> line, char separator, char delimiter)
	{
		try (StringWriter writer = new StringWriter();
			CSVFormatter formatter = CSVFormatter.of(writer, separator, delimiter))
		{
			formatter.write(line);
			return writer.toString();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new CSVFormatter for the specified Writer using semicolons as separators and double quotes as delimiters.
	 *
	 * @param writer the writer where the lines will be printed
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(Writer writer)
	{
		return new CSVFormatter(writer, SEPARATOR, DELIMITER);
	}

	/**
	 * Constructs a new CSVFormatter for the specified Writer.
	 *
	 * @param writer the writer where the lines will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(Writer writer, char separator, char delimiter)
	{
		return new CSVFormatter(writer, separator, delimiter);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param outputStream the OutputStream where the lines will be printed
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream)
	{
		return of(outputStream, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param outputStream the OutputStream where the lines will be printed
	 * @param charset the character set to be used
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream, Charset charset)
	{
		return of(outputStream, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param outputStream the OutputStream where the lines will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream, char separator, char delimiter)
	{
		return new CSVFormatter(new BufferedWriter(new OutputStreamWriter(outputStream, CHARSET)), separator, delimiter);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param outputStream the OutputStream where the lines will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set to be used
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream, char separator, char delimiter, Charset charset)
	{
		return new CSVFormatter(new BufferedWriter(new OutputStreamWriter(outputStream, charset)), SEPARATOR, DELIMITER);
	}

	/**
	 * Constructs a new CSVFormatter for the specified URL using semicolons as separators and double quotes as delimiters.
	 *
	 * @param resource URL from where the CSV rows will be printed
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(URL resource)
	{
		return of(resource, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param resource URL from where the CSV rows will be printed
	 * @param charset the character set to be used
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(URL resource, Charset charset)
	{
		return of(resource, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param resource URL from where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(URL resource, char separator, char delimiter)
	{
		return of(resource, separator, delimiter, CHARSET);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param resource URL from where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set to be used
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(URL resource, char separator, char delimiter, Charset charset)
	{
		try
		{
			return new CSVFormatter(new BufferedWriter(new FileWriter(new File(resource.getFile()), charset)), SEPARATOR, DELIMITER);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new CSVFormatter for the specified File.
	 *
	 * @param file where the CSV rows will be printed
	 *
	 * @return the new CSVFormatter created
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static CSVFormatter of(File file)
	{
		return of(file, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Constructs a new CSVFormatter for the specified File.
	 *
	 * @param file where the CSV rows will be printed
	 * @param charset the character encoding of the specified output stream
	 *
	 * @return the new CSVFormatter created
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static CSVFormatter of(File file, Charset charset)
	{
		return of(file, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Constructs a new CSVFormatter for the specified File.
	 *
	 * @param file where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVFormatter created
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static CSVFormatter of(File file, char separator, char delimiter)
	{
		return of(file, separator, delimiter, CHARSET);
	}

	/**
	 * Constructs a new CSVFormatter for the specified File.
	 *
	 * @param file where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified output stream
	 *
	 * @return the new CSVFormatter created
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static CSVFormatter of(File file, char separator, char delimiter, Charset charset)
	{
		try
		{
			return new CSVFormatter(new BufferedWriter(new FileWriter(file, charset)), separator, delimiter);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Prints the specified rows into a resource.
	 *
	 * @param rows the rows to be printed
	 * @param resource where the CSV rows will be printed
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, URL resource)
	{
		try (CSVFormatter formatter = CSVFormatter.of(resource, SEPARATOR, DELIMITER, CHARSET))
		{
			rows.forEach(row -> formatter.writeLine(row));
		}
	}

	/**
	 * Prints the specified rows into a resource.
	 *
	 * @param rows the rows to be printed
	 * @param resource where the CSV rows will be printed
	 * @param charset the character encoding of the specified resource
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, URL resource, Charset charset)
	{
		print(rows, resource, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Prints the specified rows into a resource.
	 *
	 * @param rows the rows to be printed
	 * @param resource where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, URL resource, char separator, char delimiter)
	{
		print(rows, resource, separator, delimiter, CHARSET);
	}

	/**
	 * Prints the specified rows into a resource.
	 *
	 * @param rows the rows to be printed
	 * @param resource where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified resource
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, URL resource, char separator, char delimiter, Charset charset)
	{
		try (CSVFormatter formatter = CSVFormatter.of(resource, separator, delimiter, charset))
		{
			rows.forEach(row -> formatter.writeLine(row));
		}
	}

	/**
	 * Prints the specified rows into a File.
	 *
	 * @param rows the rows to be printed
	 * @param file where the CSV rows will be printed
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, File file)
	{
		print(rows, file, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Prints the specified rows into a File.
	 *
	 * @param rows the rows to be printed
	 * @param file where the CSV rows will be printed
	 * @param charset the character encoding of the specified output stream
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, File file, Charset charset)
	{
		print(rows, file, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Prints the specified rows into a File.
	 *
	 * @param rows the rows to be printed
	 * @param file where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, File file, char separator, char delimiter)
	{
		print(rows, file, separator, delimiter, CHARSET);
	}

	/**
	 * Prints the specified rows into a File.
	 *
	 * @param rows the rows to be printed
	 * @param file where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified output stream
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, File file, char separator, char delimiter, Charset charset)
	{
		try (CSVFormatter formatter = CSVFormatter.of(file, separator, delimiter, charset))
		{
			rows.forEach(row -> formatter.writeLine(row));
		}
	}

	/**
	 * Prints the specified rows into a OutputStream.
	 *
	 * @param rows the rows to be printed
	 * @param outputStream OutputStream where the CSV rows will be printed
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, OutputStream outputStream)
	{
		print(rows, outputStream, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Prints the specified rows into a OutputStream.
	 *
	 * @param rows the rows to be printed
	 * @param outputStream OutputStream where the CSV rows will be printed
	 * @param charset the character encoding of the specified output stream
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, OutputStream outputStream, Charset charset)
	{
		print(rows, outputStream, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Prints the specified rows into a OutputStream.
	 *
	 * @param rows the rows to be printed
	 * @param outputStream OutputStream where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, OutputStream outputStream, char separator, char delimiter)
	{
		print(rows, outputStream, separator, delimiter, CHARSET);
	}

	/**
	 * Prints the specified rows into a OutputStream.
	 *
	 * @param rows the rows to be printed
	 * @param outputStream OutputStream where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified output stream
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, OutputStream outputStream, char separator, char delimiter, Charset charset)
	{
		try
		{
			CSVFormatter formatter = CSVFormatter.of(outputStream, separator, delimiter, charset);
			rows.forEach(row -> formatter.writeLine(row));
			outputStream.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Prints the specified rows into a OutputStream.
	 *
	 * @param rows the rows to be printed
	 * @param writer Writer where the CSV rows will be printed
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, Writer writer)
	{
		print(rows, writer, SEPARATOR, DELIMITER);
	}

	/**
	 * Prints the specified rows into a OutputStream.
	 *
	 * @param rows the rows to be printed
	 * @param writer Writer where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @throws java.io.UncheckedIOException if a IOException is thrown when writing to the file
	 */
	public static void print(List<List<String>> rows, Writer writer, char separator, char delimiter)
	{
		try
		{
			CSVFormatter formatter = CSVFormatter.of(writer, separator, delimiter);
			rows.forEach(row -> formatter.writeLine(row));
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
