package gate.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.function.Consumer;

public class LineSpliterator implements Spliterator<String>
{

	private final BufferedReader reader;

	public LineSpliterator(InputStream is)
	{
		this.reader = new BufferedReader(new InputStreamReader(is));
	}

	@Override
	public boolean tryAdvance(Consumer<? super String> action)
	{
		try
		{
			String line = reader.readLine();
			if (line == null)
				return false;
			action.accept(line);
			return true;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Spliterator<String> trySplit()
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
		return NONNULL | IMMUTABLE;
	}
}
