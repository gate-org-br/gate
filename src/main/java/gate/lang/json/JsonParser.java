package gate.lang.json;

import gate.error.AppError;
import gate.error.ConversionException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads the JSON elements from a specified source.
 */
public class JsonParser implements AutoCloseable, Iterable<JsonElement>
{

	private final JsonScanner scanner;

	/**
	 * Reads the JSON elements from a JSONScanner.
	 *
	 * @param scanner JSONScanner to be read
	 */
	public JsonParser(JsonScanner scanner)
	{
		this.scanner = scanner;
	}

	/**
	 * Reads the JSON elements from a Reader.
	 *
	 * @param reader Reader to be read
	 *
	 * @throws gate.error.ConversionException if an error occurs while trying to parse the input
	 */
	public JsonParser(Reader reader) throws ConversionException
	{
		this.scanner = new JsonScanner(reader);
	}

	/**
	 * Reads the JSON elements from a String.
	 *
	 * @param string string to be read
	 *
	 * @throws gate.error.ConversionException if an error occurs while trying to parse the input
	 */
	public JsonParser(String string) throws ConversionException
	{
		this(new JsonScanner(string));
	}

	/**
	 * Reads the next JSON element from the specified source as a java object.
	 * <p>
	 * Each type of JSON object is returned as a java object of the corresponding type:
	 * <ul>
	 * <li>JSON boolean: {@link gate.lang.json.JsonBoolean}
	 * <li>JSON string: {@link gate.lang.json.JsonString}
	 * <li>JSON number: {@link gate.lang.json.JsonNumber}
	 * <li>JSON array: {@link gate.lang.json.JsonArray}
	 * <li>JSON object: {@link gate.lang.json.JsonObject}
	 * <li>JSON null: {@link gate.lang.json.JsonNull}
	 * </ul>
	 *
	 * @return an Optional describing the JSON object read or an empty optional if there are no more JSON objects on the* input stream.
	 *
	 * @throws gate.error.ConversionException if any error occurs
	 */
	public Optional<JsonElement> parse() throws ConversionException
	{
		if (scanner.getCurrent().getType() == JsonToken.Type.EOF)
			return Optional.empty();
		return Optional.of(element());
	}

	@Override
	public void close()
	{
		try
		{
			scanner.close();
		} catch (Exception ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	public Iterator<JsonElement> iterator()
	{
		return new JsonParserIterator();
	}

	@Override
	public void forEach(Consumer<? super JsonElement> action)
	{
		while (scanner.getCurrent().getType() == JsonToken.Type.EOF)
			try
			{
				action.accept(parse().get());
			} catch (ConversionException ex)
			{
				throw new AppError(ex);
			}
	}

	@Override
	public Spliterator<JsonElement> spliterator()
	{
		return new JsonParserSpliterator();
	}

	/**
	 * Returns a sequential Stream with this parser as its source.
	 *
	 * @return a sequential Stream over the elements in this parser
	 */
	public Stream<JsonElement> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}

	private JsonElement element() throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case NUMBER:
				return number();
			case TRUE:
			case FALSE:
				return bool();
			case STRING:
				return string();
			case OPEN_OBJECT:
				return object();
			case NULL:
				return JsonNull.INSTANCE;
			case OPEN_ARRAY:
				return array();
			default:
				throw new ConversionException("Expected boolean, number, string, array or object and found " + scanner
					.getCurrent());
		}
	}

	private JsonObject object() throws ConversionException
	{
		JsonObject object = new JsonObject();

		do
		{
			scanner.scan();

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
			{
				if (scanner.getCurrent().getType() != JsonToken.Type.STRING)
					throw new ConversionException("Expected key and found " + scanner.getCurrent());

				String key = scanner.getCurrent().toString();

				scanner.scan();

				if (scanner.getCurrent().getType() != JsonToken.Type.DOUBLE_DOT)
					throw new ConversionException("Expected ':' and found " + scanner.getCurrent());

				scanner.scan();
				object.put(key, element());
			} else if (!object.isEmpty())
				throw new ConversionException("Expected element and found " + scanner.getCurrent());

		} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
			throw new ConversionException("Expected '}' and found " + scanner.getCurrent());

		scanner.scan();
		return object;
	}

	private JsonArray array() throws ConversionException
	{
		JsonArray array = new JsonArray();

		do
		{
			scanner.scan();
			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				array.add(element());
			else if (!array.isEmpty())
				throw new ConversionException("Expected element and found " + scanner.getCurrent());
		} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
			throw new ConversionException("Expected ']' and found " + scanner.getCurrent());

		scanner.scan();
		return array;
	}

	private JsonNumber number() throws ConversionException
	{
		JsonNumber value = JsonNumber.of(scanner.getCurrent().toString());
		scanner.scan();
		return value;
	}

	private JsonString string() throws ConversionException
	{
		JsonString value = JsonString.of(scanner.getCurrent().toString());
		scanner.scan();
		return value;
	}

	private JsonBoolean bool() throws ConversionException
	{
		switch (scanner.getCurrent().getType())
		{
			case TRUE:
				scanner.scan();
				return JsonBoolean.TRUE;
			case FALSE:
				scanner.scan();
				return JsonBoolean.FALSE;
			default:
				throw new ConversionException(scanner.getCurrent() + " is not a boolean");
		}
	}

	private class JsonParserSpliterator implements Spliterator<JsonElement>
	{

		@Override
		public boolean tryAdvance(Consumer<? super JsonElement> action)
		{
			try
			{
				if (scanner.getCurrent().getType() == JsonToken.Type.EOF)
					return false;
				parse().ifPresent(action);
				return true;
			} catch (ConversionException ex)
			{
				throw new AppError(ex);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super JsonElement> action)
		{
			while (scanner.getCurrent().getType()
				!= JsonToken.Type.EOF)
				try
				{
					parse().ifPresent(action);
				} catch (ConversionException ex)
				{
					throw new AppError(ex);
				}
		}

		@Override
		public Spliterator<JsonElement> trySplit()
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

	private class JsonParserIterator implements Iterator<JsonElement>
	{

		@Override
		public boolean hasNext()
		{
			return scanner.getCurrent().getType()
				!= JsonToken.Type.EOF;
		}

		@Override
		public void forEachRemaining(Consumer<? super JsonElement> action)
		{
			while (scanner.getCurrent().getType()
				!= JsonToken.Type.EOF)
				try
				{
					parse().ifPresent(action);
				} catch (ConversionException ex)
				{
					throw new AppError(ex);
				}
		}

		@Override
		public JsonElement next()
		{
			try
			{
				return parse().get();
			} catch (ConversionException ex)
			{
				throw new AppError(ex);
			}
		}
	}
}
