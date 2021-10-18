package gate.lang.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
	private final char separator, delimiter;
	private final StringBuilder string = new StringBuilder();

	private final static char SEPARATOR = ';';
	private final static char DELIMITER = '"';
	private final static Charset CHARSET = Charset.forName("UTF-8");

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
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(Reader reader)
	{
		return of(reader, SEPARATOR, DELIMITER);

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
	public static CSVParser of(Reader reader, char separator, char delimiter)
	{
		return new CSVParser(new BufferedReader(reader), separator, delimiter);
	}

	/**
	 * Read the contents of the specified reader as a list.
	 *
	 * @param reader Reader from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(Reader reader)
	{
		try ( CSVParser parser = CSVParser.of(reader))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified reader as a list.
	 *
	 * @param reader Reader from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(Reader reader, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(reader, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified reader as a set.
	 *
	 * @param reader Reader from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(Reader reader)
	{
		try ( CSVParser parser = CSVParser.of(reader))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified reader as a set.
	 *
	 * @param reader Reader from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(Reader reader, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(reader, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Constructs a new CSVParser for the specified BufferedReader.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(BufferedReader reader)
	{
		return of(reader, SEPARATOR, DELIMITER);
	}

	/**
	 * Constructs a new CSVParser for the specified BufferedReader.
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
	 * Read the contents of the specified BufferedReader as a list.
	 *
	 * @param reader BufferedReader from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(BufferedReader reader)
	{
		try ( CSVParser parser = CSVParser.of(reader))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified BufferedReader as a list.
	 *
	 * @param reader BufferedReader from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(BufferedReader reader, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(reader, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified BufferedReader as a set.
	 *
	 * @param reader BufferedReader from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(BufferedReader reader)
	{
		try ( CSVParser parser = CSVParser.of(reader))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified BufferedReader as a set.
	 *
	 * @param reader BufferedReader from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(BufferedReader reader, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(reader, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream.
	 *
	 * @param inputStream InputStream from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(InputStream inputStream)
	{
		return of(inputStream, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Constructs a new CSVParser for the specified InputStream.
	 *
	 * @param inputStream the inputStream from where the CSV rows will be extracted
	 * @param charset the character encoding of the specified input stream
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(InputStream inputStream, Charset charset)
	{
		return of(inputStream, SEPARATOR, DELIMITER, charset);
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
		return of(inputStream, separator, delimiter, CHARSET);
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

	/**
	 * Read the contents of the specified inputStream as a list.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(InputStream inputStream)
	{
		try ( CSVParser parser = CSVParser.of(inputStream))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a list.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(InputStream inputStream, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(inputStream, charset))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a list.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(InputStream inputStream, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(inputStream, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a list.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(InputStream inputStream, char separator, char delimiter, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(inputStream, separator, delimiter, charset))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a set.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(InputStream inputStream)
	{
		try ( CSVParser parser = CSVParser.of(inputStream))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a set.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(InputStream inputStream, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(inputStream, charset))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a set.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(InputStream inputStream, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(inputStream, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified inputStream as a set.
	 *
	 * @param inputStream the InputStream from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(InputStream inputStream, char separator, char delimiter, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(inputStream, separator, delimiter, charset))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Constructs a new CSVParser for the specified URL.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(URL resource)
	{
		return of(resource, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Constructs a new CSVParser for the specified URL.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param charset the character set of the resource
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(URL resource, Charset charset)
	{
		return of(resource, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Constructs a new CSVParser for the specified URL.
	 *
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(URL resource, char separator, char delimiter)
	{
		return of(resource, separator, delimiter, CHARSET);
	}

	/**
	 * Constructs a new CSVParser for the specified URL.
	 *
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the resource
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(URL resource, char separator, char delimiter, Charset charset)
	{
		try
		{
			return new CSVParser(new BufferedReader(new InputStreamReader(resource.openStream(), charset)), separator, delimiter);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Read the contents of the specified resource as a list.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(URL resource)
	{
		try ( CSVParser parser = CSVParser.of(resource))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified resource as a list.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param charset the character set of the resource
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(URL resource, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(resource, charset))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified resource as a list.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(URL resource, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(resource, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified resource as a list.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the resource
	 *
	 * @return the new CSVParser created
	 */
	public static List<Row> read(URL resource, char separator, char delimiter, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(resource, separator, delimiter, charset))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified resource as a set.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(URL resource)
	{
		try ( CSVParser parser = CSVParser.of(resource))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified resource as a set.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param charset the character set of the resource
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(URL resource, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(resource, charset))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified resource as a set.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(URL resource, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(resource, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified resource as a set.
	 *
	 * @param resource URL from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the resource
	 *
	 * @return the new CSVParser created
	 */
	public static Set<Row> distinct(URL resource, char separator, char delimiter, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(resource, separator, delimiter, charset))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Constructs a new CSVParser for the specified File.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param charset the character set of the file
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(File file, Charset charset)
	{
		return of(file, SEPARATOR, DELIMITER, charset);
	}

	/**
	 * Constructs a new CSVParser for the specified File.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(File file)
	{
		return of(file, SEPARATOR, DELIMITER, CHARSET);
	}

	/**
	 * Constructs a new CSVParser for the specified File.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(File file, char separator, char delimiter)
	{
		return of(file, separator, delimiter, CHARSET);
	}

	/**
	 * Constructs a new CSVParser for the specified File.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the file
	 *
	 * @return the new CSVParser created
	 */
	public static CSVParser of(File file, char separator, char delimiter, Charset charset)
	{
		try
		{
			return new CSVParser(new BufferedReader(new FileReader(file, charset)), separator, delimiter);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Read the contents of the specified file as a list.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(File file)
	{
		try ( CSVParser parser = CSVParser.of(file))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified file as a list.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(File file, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(file, charset))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified file as a list.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(File file, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(file, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified file as a list.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static List<Row> read(File file, char separator, char delimiter, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(file, separator, delimiter, charset))
		{
			return parser.stream().collect(Collectors.toList());
		}
	}

	/**
	 * Read the contents of the specified file as a set.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(File file)
	{
		try ( CSVParser parser = CSVParser.of(file))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified file as a set.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(File file, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(file, charset))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified file as a set.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(File file, char separator, char delimiter)
	{
		try ( CSVParser parser = CSVParser.of(file, separator, delimiter))
		{
			return parser.stream().collect(Collectors.toSet());
		}
	}

	/**
	 * Read the contents of the specified file as a set.
	 *
	 * @param file the File from where the CSV rows will be extracted
	 * @param separator the character used as field separator
	 * @param delimiter the character used as field delimiter
	 * @param charset the character set of the file
	 *
	 * @return the rows of the CSV file as a list
	 */
	public static Set<Row> distinct(File file, char separator, char delimiter, Charset charset)
	{
		try ( CSVParser parser = CSVParser.of(file, separator, delimiter, charset))
		{
			return parser.stream().collect(Collectors.toSet());
		}
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
		while (!isEOL()
			&& line.charAt(indx) != separator
			&& line.charAt(indx) != delimiter
			&& Character.isWhitespace(line.charAt(indx)))
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
		try ( CSVParser parser = CSVParser.of(new BufferedReader(new StringReader(string))))
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
		try ( CSVParser parser = new CSVParser(new BufferedReader(new StringReader(string)), separator, delimiter))
		{
			return parser.parse();
		}
	}

}
