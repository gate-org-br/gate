package gate.io;

import gate.lang.json.JsonElement;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface Readable
{

	<T> T read(Reader<T> reader) throws IOException;

	default List<String> readLines() throws IOException
	{
		return read(LineReader.getInstance());
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

	default List<List<String>> readCSV() throws IOException
	{
		return read(CSVReader.getInstance());
	}
}
