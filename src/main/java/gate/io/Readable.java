package gate.io;

import gate.lang.json.JsonElement;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface Readable
{

	public <T> T read(Reader<T> reader) throws IOException;

	public default List<String> readLines() throws IOException
	{
		return read(LineReader.getInstance());
	}

	public default <T> Optional<T> readObject(Class<T> type) throws IOException
	{
		return read(ObjectReader.getInstance(type));
	}

	public default Optional<JsonElement> readJsonElement() throws IOException
	{
		return read(JsonElementReader.getInstance());
	}

	public default String readString() throws IOException
	{
		return read(StringReader.getInstance());
	}

	public default List<String> readLines(String charset) throws IOException
	{
		return read(LineReader.getInstance(charset));
	}

	public default String readString(String charset) throws IOException
	{
		return read(StringReader.getInstance(charset));
	}
}
