package gate.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Streamable
{

	public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator) throws IOException;

	public default Stream<String> lineStream() throws IOException
	{
		return stream(inputStream -> new LineSpliterator(inputStream));
	}

	public default Stream<List<String>> csvStream() throws IOException
	{
		return stream(inputStream -> new CSVSpliterator(inputStream));
	}
}
