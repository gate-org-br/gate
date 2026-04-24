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

	private PSVParser(BufferedReader reader, int... positions)
	{
		for (int i = 1; i < positions.length; i++)
			if (positions[i] < positions[i - 1])
				throw new IllegalArgumentException("Invalid positions specified");

		this.reader = reader;
		this.positions = positions.clone();
		readLine();
	}

	public static PSVParser of(BufferedReader reader, int... positions) throws IOException
	{
		return new PSVParser(reader, positions);
	}

	public static PSVParser of(URL resource, int... positions) throws IOException
	{
		return new PSVParser(new BufferedReader(new InputStreamReader(resource.openStream())), positions);
	}

	public static PSVParser of(InputStream inputStream, int... positions) throws IOException
	{
		return new PSVParser(new BufferedReader(new InputStreamReader(inputStream)), positions);
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

	private Row parse()
	{
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

		readLine();
		return result;
	}

	@Override
	public long skip(long lines)
	{
		while (line != null & lines > 0)
		{
			readLine();
			lines--;
			lineNumber++;
		}

		return lines;
	}

	@Override
	public long processed()
	{
		return lineNumber + 1;
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
	public void forEach(Consumer<? super Row> action)
	{
		for (Row row = parse(); row != null; row = parse())
			action.accept(row);
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
	 * Parses a PSV string
	 *
	 * @param string the string to be parsed
	 * @param columns a list with the index of each column
	 * @return a List with all the columns contained in the specified string
	 */
	public static Row parseLine(String string, int... columns)
	{
		try ( PSVParser parser = new PSVParser(new BufferedReader(new StringReader(string)), columns))
		{
			return parser.parse();
		}
	}
}
