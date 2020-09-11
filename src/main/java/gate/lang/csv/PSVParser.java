package gate.lang.csv;

import gate.error.AppError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts Lists of strings from a position based formatted
 * {@link java.io.Reader}.
 */
public class PSVParser implements Parser
{

	private String line;
	private long lineNumber = -1;
	private final int[] positions;
	private final BufferedReader reader;

	public PSVParser(BufferedReader reader, int... positions)
	{
		for (int i = 1; i < positions.length; i++)
			if (positions[i] < positions[i - 1])
				throw new IllegalArgumentException("Invalid positions specified");

		this.reader = reader;
		this.positions = positions.clone();
	}

	public PSVParser(URL resource, int... positions) throws IOException
	{
		this(new BufferedReader(new InputStreamReader(resource.openStream())), positions);
	}

	public PSVParser(InputStream inputStream, int... positions) throws IOException
	{
		this(new BufferedReader(new InputStreamReader(inputStream)), positions);
	}

	@Override
	public Optional<Row> parseLine() throws IOException
	{
		return Optional.ofNullable(parse());
	}

	private Row parse() throws IOException
	{
		line = reader.readLine();
		if (line == null)
			return null;

		Row result = new Row(++lineNumber, line);

		for (int i = 0; i < positions.length; i++)
		{
			if (i < positions.length - 1)
			{
				if (line.length() > positions[i + 1])
					result.add(line.substring(positions[i],
						positions[i + 1]));
			} else if (line.length() > positions[i])
				result.add(line.substring(positions[i]));
		}

		return result;
	}

	@Override
	public long skip(long lines) throws IOException
	{
		while (lines > 0)
		{
			if (reader.readLine() == null)
				break;
			lines--;
			lineNumber++;
		}

		return lines;
	}

	@Override
	public Stream<Row> stream()
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
	 * Parses a PSV string
	 *
	 * @param string the string to be parsed
	 * @param columns a list with the index of each column
	 * @return a List with all the columns contained in the specified string
	 */
	public static Row parseLine(String string, int... columns)
	{
		try (PSVParser parser = new PSVParser(new BufferedReader(new StringReader(string)), columns))
		{
			return parser.parse();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
