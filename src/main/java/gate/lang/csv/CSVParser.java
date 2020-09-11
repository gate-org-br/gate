package gate.lang.csv;

import gate.error.AppError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts Lists of strings from a CSV formatted {@link java.io.Reader}.
 */
public class CSVParser implements Parser
{

	private int indx;
	private String line;
	private long lineNumber = -1;
	private final BufferedReader reader;
	private final int separator, delimiter;
	private final StringBuilder string = new StringBuilder();

	/**
	 * Constructs a new CSVParser for the specified Reader using semicolons
	 * as separators and double quotes as delimiters.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 */
	public CSVParser(BufferedReader reader)
	{
		this(reader, ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream using
	 * semicolons as separators and double quotes as delimiters.
	 *
	 * @param inputStream InputStream from where the CSV rows will be
	 * extracted
	 */
	public CSVParser(InputStream inputStream)
	{
		this(inputStream, ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified URL using semicolons as
	 * separators and double quotes as delimiters.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 *
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public CSVParser(URL resource) throws IOException
	{
		this(new BufferedReader(new InputStreamReader(resource.openStream())));
	}

	/**
	 * Constructs a new CSVParser for the specified URL
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @throws java.io.IOException If an I/O error occurs
	 */
	public CSVParser(URL resource, char separator, char delimiter) throws IOException
	{
		this(new BufferedReader(new InputStreamReader(resource.openStream())), separator, delimiter);
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream using
	 * semicolons as separators and double quotes as delimiters and
	 * encoding.
	 *
	 * @param inputStream inputStream InputStream from where the CSV rows
	 * will be extracted
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
	 * @param inputStream the inputStream from where the CSV rows will be
	 * extracted
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
	 * @param inputStream the inputStream from where the CSV rows will be
	 * extracted
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

	@Override
	public Optional<Row> parseLine() throws IOException
	{
		return Optional.ofNullable(parse());
	}

	@Override
	public long skip(long lines) throws IOException
	{
		while (line != null && lines > 0)
		{
			line = reader.readLine();
			lines--;
			lineNumber++;
		}

		return lines;
	}

	public boolean isDoubleDelimiter() throws IOException
	{
		return !isEOL()
			& line.charAt(indx) == delimiter
			&& indx + 1 < line.length()
			&& line.charAt(indx + 1) != delimiter;
	}

	private Row parse() throws IOException
	{
		indx = 0;
		line = reader.readLine();
		if (line == null)
			return null;

		Row result = new Row(++lineNumber, line);

		if (!isEOL())
		{
			result.add(field());
			while (!isEOL() && line.charAt(indx) == separator)
			{
				indx++;
				result.add(field());
			}
		}

		return result;
	}

	private void skipSpaces() throws IOException
	{
		while (!isEOL() && Character.isWhitespace(line.charAt(indx)))
			indx++;
	}

	private String field() throws IOException
	{
		skipSpaces();

		if (isEOL())
			return "";

		String field = line.charAt(indx) == delimiter ? delimited() : normal();

		skipSpaces();

		return field;
	}

	public String normal() throws IOException
	{
		string.setLength(0);
		while (!isEOL() && line.charAt(indx) != separator)
			string.append(line.charAt(indx++));
		return string.toString().trim();
	}

	public String delimited() throws IOException
	{
		indx++;
		string.setLength(0);
		while (!isEOL())
		{
			if (line.charAt(indx) == delimiter)
			{
				if (indx + 1 == line.length()
					|| line.charAt(indx + 1) != delimiter)
					break;

				indx++;
			}

			string.append(line.charAt(indx++));
		}

		indx++;

		while (!isEOL() && line.charAt(indx) != separator)
			indx++;

		return string.toString().trim();
	}

	private boolean isEOL() throws IOException
	{
		return indx == line.length();
	}

	@Override
	public Stream<Row> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public void forEach(Consumer<? super Row> action)
	{
		try
		{
			for (Row line = parse(); line != null; line = parse())
				action.accept(line);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void close()
	{
		try
		{
			reader.close();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Iterator<Row> iterator()
	{
		return new Iterator<Row>()
		{

			@Override
			public boolean hasNext()
			{
				try
				{
					reader.mark(1);
					boolean result = reader.read() != -1;
					reader.reset();
					return result;
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			}

			@Override
			public void forEachRemaining(Consumer<? super Row> action)
			{
				try
				{
					for (Row line = parse(); line != null; line = parse())
						action.accept(line);
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			}

			@Override
			public Row next()
			{
				try
				{
					return parse();
				} catch (IOException ex)
				{
					throw new AppError(ex);
				}
			}
		};
	}

	@Override
	public Spliterator<Row> spliterator()
	{
		return new Spliterator<Row>()
		{

			@Override
			public boolean tryAdvance(Consumer<? super Row> action)
			{
				try
				{
					Row line = parse();
					if (line == null)
						return false;
					action.accept(line);
					return true;
				} catch (IOException ex)
				{
					throw new AppError(ex);
				}
			}

			@Override
			public void forEachRemaining(Consumer<? super Row> action)
			{
				try
				{
					for (Row line = parse(); line != null; line = parse())
						action.accept(line);
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			}

			@Override
			public Spliterator<Row> trySplit()
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
		};
	}

	/**
	 * Parses a CSV string
	 *
	 * @param string the string to be parsed
	 * @return a List with all the columns contained in the specified string
	 */
	public static Row parseLine(String string)
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new StringReader(string))))
		{
			return parser.parse();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Parses a CSV string using the specified separator and delimiter
	 *
	 * @param string the string to be parsed
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return a List with all the columns contained in the specified string
	 */
	public static Row parseLine(String string, char separator, char delimiter)
	{
		try (CSVParser parser = new CSVParser(new BufferedReader(new StringReader(string)), separator, delimiter))
		{
			return parser.parse();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
