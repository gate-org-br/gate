package gate.lang.csv;

import gate.error.AppError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts Lists of strings from a CSV formatted {@link java.io.Reader}.
 */
public class CSVParser implements AutoCloseable, Iterable<List<String>>
{

	private long lineNumber = -1;
	private int c = Integer.MAX_VALUE;
	private final BufferedReader reader;
	private final int separator, delimiter;
	private final StringBuilder string = new StringBuilder();
	private static final Optional EMPTY = Optional.of(List.of());

	/**
	 * Constructs a new CSVParser for the specified Reader using semicolons as separators and double quotes as delimiters.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 */
	public CSVParser(BufferedReader reader)
	{
		this(reader, ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream using semicolons as separators and double quotes as delimiters.
	 *
	 * @param inputStream inputStream InputStream from where the CSV rows will be extracted
	 */
	public CSVParser(InputStream inputStream)
	{
		this(inputStream, ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream using semicolons as separators and double quotes as delimiters and encoding.
	 *
	 * @param inputStream inputStream InputStream from where the CSV rows will be extracted
	 * @param charset the character encoding of the specified input stream
	 */
	public CSVParser(InputStream inputStream, Charset charset)
	{
		this(inputStream, ';', '\"', charset);
	}

	/**
	 * Constructs a new CSVParser for the specified Reader.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 */
	public CSVParser(BufferedReader reader, char separator, char delimiter)
	{
		this.reader = reader;
		this.delimiter = delimiter;
		this.separator = separator;
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream.
	 *
	 * @param inputStream the inputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 */
	public CSVParser(InputStream inputStream, char separator, char delimiter)
	{
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.delimiter = delimiter;
		this.separator = separator;
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream.
	 *
	 * @param inputStream the inputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified input stream
	 */
	public CSVParser(InputStream inputStream, char separator, char delimiter, Charset charset)
	{
		this.reader = new BufferedReader(new InputStreamReader(inputStream, charset));
		this.delimiter = delimiter;
		this.separator = separator;
	}

	/**
	 * Extracts the next line of the previously specified CSV formatted {@link java.io.Reader} as a String List.
	 *
	 * @return an Optional describing the row returned as a String List of an empty Optional if there are no more rows to be read
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public Optional<List<String>> parseLine() throws IOException
	{
		if (c == -1)
			return Optional.empty();

		do
			read();
		while (c == '\r');

		if (c == -1 || c == '\n')
			return EMPTY;

		return Optional.of(line());
	}

	/**
	 * Try to skip a number of lines from the previously specified CSV formatted {@link java.io.Reader}.
	 *
	 * @param lines the number of lines to be skipped
	 *
	 * @return the number of lines not skipped
	 *
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public long skip(long lines) throws IOException
	{
		while (c != -1 && lines > 0)
		{
			lines--;
			lineNumber++;

			do
			{
				read();
			} while (c != -1 && c != '\n');

		}

		return lines;
	}

	/**
	 * Return the current line number.
	 *
	 * @return the current line number
	 */
	public long getLineNumber()
	{
		return lineNumber;
	}

	public void read() throws IOException
	{
		c = reader.read();
	}

	public boolean isDelimiter() throws IOException
	{
		if (c == -1)
			return true;
		if (c != delimiter)
			return false;

		reader.mark(1);
		if (reader.read() == delimiter)
			return false;

		reader.reset();
		return true;
	}

	private List<String> line() throws IOException
	{
		List<String> line = new ArrayList<>();

		line.add(field());

		while (c == separator)
		{
			read();
			line.add(field());
		}

		lineNumber++;
		return line;
	}

	private String field() throws IOException
	{
		while (c == ' ' || c == '\t' || c == '\r')
			read();

		String field = c == delimiter
			? delimited() : normal();

		while (c == ' ' || c == '\t' || c == '\r')
			read();
		return field;
	}

	public String normal() throws IOException
	{
		string.setLength(0);
		for (; c != -1
			&& c != '\n'
			&& c != separator; read())
			string.append((char) c);
		return string.toString().trim();
	}

	public String delimited() throws IOException
	{
		string.setLength(0);
		for (read(); !isDelimiter(); read())
			string.append((char) c);
		read();
		return string.toString().trim();
	}

	/**
	 * Returns a sequential Stream with this parser as its source.
	 *
	 * @return a sequential Stream over the elements in this parser
	 */
	public Stream<List<String>> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public void close()
	{
		try
		{
			reader.close();
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public Iterator<List<String>> iterator()
	{
		return new CSVParserIterator();
	}

	@Override
	public void forEach(Consumer<? super List<String>> action)
	{
		while (c != -1)
			try
		{
			parseLine().ifPresent(action);
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	public Spliterator<List<String>> spliterator()
	{
		return new CSVParserSpliterator();
	}

	private class CSVParserSpliterator implements Spliterator<List<String>>
	{

		@Override
		public boolean tryAdvance(Consumer<? super List<String>> action)
		{
			try
			{
				if (c == -1)
					return false;
				parseLine().ifPresent(action);
				return true;
			} catch (IOException ex)
			{
				throw new AppError(ex);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super List<String>> action)
		{
			while (c != -1)
				try
			{
				parseLine().ifPresent(action);
			} catch (IOException ex)
			{
				throw new AppError(ex);
			}
		}

		@Override
		public Spliterator<List<String>> trySplit()
		{
			return null;
		}

		@Override
		public long estimateSize()
		{
			return Long.MAX_VALUE;
		}

		@Override
		public int characteristics()
		{
			return Spliterator.ORDERED | Spliterator.NONNULL;
		}
	}

	private class CSVParserIterator implements Iterator<List<String>>
	{

		@Override
		public boolean hasNext()
		{
			return c != -1;
		}

		@Override
		public void forEachRemaining(Consumer<? super List<String>> action)
		{
			while (c != -1)
				try
			{
				parseLine().ifPresent(action);
			} catch (IOException ex)
			{
				throw new AppError(ex);
			}
		}

		@Override
		public List<String> next()
		{
			try
			{
				return parseLine().get();
			} catch (IOException ex)
			{
				throw new AppError(ex);
			}
		}
	}
}
