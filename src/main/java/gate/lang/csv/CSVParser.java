package gate.lang.csv;

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

	private CSVParser(BufferedReader reader, char separator, char delimiter)
	{
		this.reader = reader;
		this.delimiter = delimiter;
		this.separator = separator;
		readLine();
	}

	/**
	 * Constructs a new CSVParser for the specified Reader.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(BufferedReader reader, char separator, char delimiter)
	{
		return new CSVParser(reader, separator, delimiter);
	}

	/**
	 * Constructs a new CSVParser for the specified Reader using semicolons as separators and double quotes as delimiters.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(BufferedReader reader)
	{
		return new CSVParser(reader, ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream using semicolons as separators and double quotes as delimiters.
	 *
	 * @param inputStream InputStream from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(InputStream inputStream)
	{
		return new CSVParser(new BufferedReader(new InputStreamReader(inputStream)), ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified URL using semicolons as separators and double quotes as delimiters.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(URL resource)
	{
		try
		{
			return new CSVParser(new BufferedReader(new InputStreamReader(resource.openStream())), ';', '\"');
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new CSVParser for the specified URL
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(URL resource, char separator, char delimiter)
	{
		try
		{
			return new CSVParser(new BufferedReader(new InputStreamReader(resource.openStream())), separator, delimiter);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream using semicolons as separators and double quotes as delimiters and encoding.
	 *
	 * @param inputStream inputStream InputStream from where the CSV rows will be extracted
	 * @param charset the character encoding of the specified input stream
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(InputStream inputStream, Charset charset)
	{
		return new CSVParser(new BufferedReader(new InputStreamReader(inputStream, charset)), ';', '\"');
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream.
	 *
	 * @param inputStream the inputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(InputStream inputStream, char separator, char delimiter)
	{
		return new CSVParser(new BufferedReader(new InputStreamReader(inputStream)), separator, delimiter);
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream.
	 *
	 * @param inputStream the inputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character encoding of the specified input stream
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(InputStream inputStream, char separator, char delimiter, Charset charset)
	{
		return new CSVParser(new BufferedReader(new InputStreamReader(inputStream, charset)), separator, delimiter);
	}

	private void readLine()
	{
		try
		{
			line = reader.readLine();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Optional<Row> parseLine()
	{
		return Optional.ofNullable(parse());
	}

	@Override
	public long skip(long lines)
	{
		while (line != null && lines > 0)
		{
			readLine();
			lines--;
			lineNumber++;
		}

		return lines;
	}

	public boolean isDoubleDelimiter()
	{
		return !isEOL()
			& line.charAt(indx) == delimiter
			&& indx + 1 < line.length()
			&& line.charAt(indx + 1) != delimiter;
	}

	private Row parse()
	{
		if (line == null)
			return null;

		indx = 0;

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

		readLine();
		return result;
	}

	private void skipSpaces()
	{
		while (!isEOL() && Character.isWhitespace(line.charAt(indx)))
			indx++;
	}

	private String field()
	{
		skipSpaces();

		if (isEOL())
			return "";

		String field = line.charAt(indx) == delimiter ? delimited() : normal();

		skipSpaces();

		return field;
	}

	public String normal()
	{
		string.setLength(0);
		while (!isEOL() && line.charAt(indx) != separator)
			string.append(line.charAt(indx++));
		return string.toString().trim();
	}

	@Override
	public long processed()
	{
		return lineNumber + 1;
	}

	public String delimited()
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

		if (!isEOL())
			indx++;

		while (!isEOL() && line.charAt(indx) != separator)
			indx++;

		return string.toString().trim();
	}

	private boolean isEOL()
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
		for (Row row = parse(); row != null; row = parse())
			action.accept(row);
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
				return line != null;
			}

			@Override
			public void forEachRemaining(Consumer<? super Row> action)
			{
				for (Row line = parse(); line != null; line = parse())
					action.accept(line);
			}

			@Override
			public Row next()
			{
				return parse();
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
				Row line = parse();
				if (line == null)
					return false;
				action.accept(line);
				return true;
			}

			@Override
			public void forEachRemaining(Consumer<? super Row> action)
			{
				for (Row line = parse(); line != null; line = parse())
					action.accept(line);
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
		try (CSVParser parser = CSVParser.of(new BufferedReader(new StringReader(string))))
		{
			return parser.parse();
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
		}
	}
}
