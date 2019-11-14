package gate.lang.csv;

import gate.error.AppError;
import java.io.IOException;
import java.io.Reader;
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
 * <p>
 * CSV columns can be unquoted, single quoted or double quoted and can be separated by commas or semicolons
 */
public class CSVParser implements AutoCloseable, Iterable<List<String>>
{

	private final Reader reader;
	private int c = Integer.MAX_VALUE;
	private final StringBuilder string = new StringBuilder();

	private static final Optional EMPTY = Optional.of(List.of());

	/**
	 * Constructs a new CSVParser for the specified Reader.
	 *
	 * @param reader the reader from where the CSV rows will be extracted
	 */
	public CSVParser(Reader reader)
	{
		this.reader = reader;
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
			c = reader.read();
		while (c == '\r');

		if (c == -1 || c == '\n')
			return EMPTY;

		return Optional.of(line());
	}

	private List<String> line() throws IOException
	{
		List<String> line = new ArrayList<>();

		line.add(field());

		while (c == ',' || c == ';')
		{
			c = reader.read();
			line.add(field());
		}

		return line;
	}

	private String field() throws IOException
	{
		while (c == ' ' || c == '\t' || c == '\r')
			c = reader.read();

		String field = parse();

		while (c == ' ' || c == '\t' || c == '\r')
			c = reader.read();

		return field;
	}

	private String parse() throws IOException
	{
		switch (c)
		{
			case '\'':
				return quoted();
			case '"':
				return doubleQuoted();
			default:
				return unquoted();

		}
	}

	public String quoted() throws IOException
	{
		string.setLength(0);
		for (c = reader.read(); c != -1 && c != '\''; c = reader.read())
			string.append((char) (c == '\\' ? c = reader.read() : c));
		c = reader.read();
		return string.toString().trim();
	}

	public String doubleQuoted() throws IOException
	{
		string.setLength(0);
		for (c = reader.read(); c != -1 && c != '"'; c = reader.read())
			string.append((char) (c == '\\' ? c = reader.read() : c));
		c = reader.read();
		return string.toString().trim();
	}

	public String unquoted() throws IOException
	{
		string.setLength(0);
		for (; c != -1 && c != '\r' && c != '\n' && c != ',' && c != ';'; c = reader.read())
			string.append((char) (c == '\\' ? c = reader.read() : c));
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
