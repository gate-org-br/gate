package gate.lang.psv;

import gate.error.AppError;
import gate.lang.SVParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Extracts Lists of strings from a position based formatted
 * {@link java.io.Reader}.
 */
public class PSVParser implements SVParser
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
	public Optional<List<String>> parseLine() throws IOException
	{
		return Optional.ofNullable(parse());
	}

	private List<String> parse() throws IOException
	{
		line = reader.readLine();
		if (line == null)
			return null;

		lineNumber++;

		List<String> result = new ArrayList<>();

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
	public long getLineNumber()
	{
		return lineNumber;
	}

	@Override
	public String getParsedLine()
	{
		return line;
	}

	@Override
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
		return new Iterator<List<String>>()
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
			public void forEachRemaining(Consumer<? super List<String>> action)
			{
				try
				{
					for (List<String> line = parse(); line != null; line = parse())
						action.accept(line);
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			}

			@Override
			public List<String> next()
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
	public void forEach(Consumer<? super List<String>> action)
	{
		try
		{
			for (List<String> line = parse(); line != null; line = parse())
				action.accept(line);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Spliterator<List<String>> spliterator()
	{
		return new Spliterator<List<String>>()
		{

			@Override
			public boolean tryAdvance(Consumer<? super List<String>> action)
			{
				try
				{
					List<String> line = parse();
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
			public void forEachRemaining(Consumer<? super List<String>> action)
			{
				try
				{
					for (List<String> line = parse(); line != null; line = parse())
						action.accept(line);
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
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
		};
	}

	/**
	 * Parses a PSV string
	 *
	 * @param string the string to be parsed
	 * @param columns a list with the index of each column
	 * @return a List with all the columns contained in the specified string
	 */
	public static List<String> parseLine(String string, int... columns)
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
