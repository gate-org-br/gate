package gate.io;

import gate.lang.csv.CSVParser;
import gate.lang.csv.Row;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CSVSpliterator implements Spliterator<List<String>>
{

	private final CSVParser parser;

	public CSVSpliterator(InputStream is)
	{
		this.parser = CSVParser.of(new BufferedReader(new InputStreamReader(is)));
	}

	@Override
	public boolean tryAdvance(Consumer<? super List<String>> action)
	{

		Optional<Row> line = parser.parseLine();
		if (line.isEmpty())
			return false;
		action.accept(line.orElseThrow());
		return true;

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
		return NONNULL | IMMUTABLE;
	}
}
