package gate.lang.csv;

import gate.error.AppError;
import java.io.IOException;
import java.io.Writer;
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

	/**
	 * Constructs a new CSVFormatter for the specified Writer.
	 *
	 * @param writer the writer where CSV rows will be appended
	 */
	public CSVFormatter(Writer writer)
	{
		this.writer = writer;
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
		Objects.requireNonNull(values);
		boolean firstVal = true;
		for (String val : values)
		{
			if (!firstVal)
				writer.write(",");
			writer.write("\"");
			for (int i = 0; i < val.length(); i++)
			{
				char ch = val.charAt(i);
				if (ch == '"')
					writer.write("\\\"");
				else
					writer.write(ch);
			}
			writer.write("\"");
			firstVal = false;
		}
		writer.write(System.lineSeparator());
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

}
