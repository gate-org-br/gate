package gate.lang.csv;

import gate.error.AppError;
import java.io.BufferedWriter;
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
	 * Constructs a new CSVFormatter for the specified Writer using the default separator and default delimiter.
	 *
	 * @param writer the writer where CSV rows will be appended
	 */
	public CSVFormatter(Writer writer)
	{
		this(writer, ';', '"');
	}

	/**
	 * Appends a string list to the writer as a new CSV line.
	 *
	 * @param values the list of strings to be appended to the previously specified writer
	 *
	 * @throws java.io.IOException If an I/O error occurs
	 * @throws java.lang.NullPointerException If any of the parameters is null
	 */
	public void writeLine(List<String> values) throws IOException
	{
		write(values);
		writer.write(System.lineSeparator());
	}

	private void write(List<String> values) throws IOException
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
				if (ch == '"')
					writer.write("\\\"");
				else
					writer.write(ch);
			}
			writer.write(delimiter);
			firstVal = false;
		}
	}

	/**
	 * Appends a string array to the writer as a new CSV line.
	 *
	 * @param values the list of strings to be appended to the previously specified writer
	 *
	 * @throws java.io.IOException If an I/O error occurs
	 * @throws java.lang.NullPointerException If any of the parameters is null
	 */
	public void writeLine(String... values) throws IOException
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
			CSVFormatter formatter = new CSVFormatter(writer))
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
			CSVFormatter formatter = new CSVFormatter(writer, separator, delimiter))
		{
			formatter.write(line);
			return writer.toString();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
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
	 * Constructs a new CSVFormatter for the specified Writer using semicolons as separators and double quotes as delimiters.
	 *
	 * @param writer the writer where the lines will be printed
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(Writer writer)
	{
		return new CSVFormatter(writer, ';', '\"');
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
		return new CSVFormatter(new BufferedWriter(new OutputStreamWriter(outputStream)), ';', '\"');
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
		try
		{
			return new CSVFormatter(new BufferedWriter(new FileWriter(resource.getFile())), ';', '\"');
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new CSVFormatter for the specified URL.
	 *
	 * @param resource URL where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(URL resource, char separator, char delimiter)
	{
		try
		{
			return new CSVFormatter(new BufferedWriter(new FileWriter(resource.getFile())), separator, delimiter);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream using semicolons as separators and double quotes as delimiters.
	 *
	 * @param outputStream OutputStream where the CSV rows will be printed
	 * @param charset the character encoding of the specified output stream
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream, Charset charset)
	{
		return new CSVFormatter(new BufferedWriter(new OutputStreamWriter(outputStream, charset)), ';', '\"');
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param outputStream OutputStream where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream, char separator, char delimiter)
	{
		return new CSVFormatter(new BufferedWriter(new OutputStreamWriter(outputStream)), separator, delimiter);
	}

	/**
	 * Constructs a new CSVFormatter for the specified OutputStream.
	 *
	 * @param outputStream OutputStream where the CSV rows will be printed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified output stream
	 *
	 * @return the new CSVFormatter created
	 */
	public static CSVFormatter of(OutputStream outputStream, char separator, char delimiter, Charset charset)
	{
		return new CSVFormatter(new BufferedWriter(new OutputStreamWriter(outputStream, charset)), separator, delimiter);
	}
}
