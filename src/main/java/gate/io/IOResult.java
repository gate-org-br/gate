package gate.io;

import gate.lang.json.JsonElement;
import gate.stream.CheckedConsumer;
import gate.stream.CheckedPredicate;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

public interface IOResult
{

	<T> T read(Reader<T> reader) throws IOException;

	<T> long process(Processor<T> processor) throws IOException, InvocationTargetException;

	public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator) throws IOException;

	default List<String> readLines() throws IOException
	{
		return read(LineReader.getInstance());
	}

	default long processLines(CheckedPredicate<String> action) throws IOException, InvocationTargetException
	{
		return process(new LineProcessor(action));
	}

	default long processLines(CheckedConsumer<String> action) throws IOException, InvocationTargetException
	{
		return process(new LineProcessor(action));
	}

	public default Stream<String> lineStream() throws IOException
	{
		return stream(inputStream -> new LineSpliterator(inputStream));
	}

	default List<List<String>> readCSV() throws IOException
	{
		return read(CSVReader.getInstance());
	}

	public default Stream<List<String>> csvStream() throws IOException
	{
		return stream(inputStream -> new CSVSpliterator(inputStream));
	}

	default long processCSV(CheckedPredicate<List<String>> action) throws IOException, InvocationTargetException
	{
		return process(new CSVProcessor(action));
	}

	default long processCSV(CheckedConsumer<List<String>> action) throws IOException, InvocationTargetException
	{
		return process(new CSVProcessor(action));
	}

	default <T> Optional<T> readObject(Class<T> type) throws IOException
	{
		return read(ObjectReader.getInstance(type));
	}

	default Optional<JsonElement> readJsonElement() throws IOException
	{
		return read(JsonElementReader.getInstance());
	}

	default String readString() throws IOException
	{
		return read(StringReader.getInstance());
	}

	default List<String> readLines(String charset) throws IOException
	{
		return read(LineReader.getInstance(charset));
	}

	default String readString(String charset) throws IOException
	{
		return read(StringReader.getInstance(charset));
	}

	default List<List<String>> readCSV(String charset) throws IOException
	{
		return read(CSVReader.getInstance(charset));
	}

	default List<List<Object>> readXLS() throws IOException
	{
		return read(XLSReader.getInstance());
	}

	default List<List<String>> readODS() throws IOException
	{
		return read(ODSReader.getInstance());
	}

}
