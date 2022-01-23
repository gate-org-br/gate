package gate.lang.json;

import gate.error.AppError;
import gate.error.ConversionException;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * Writes JSON tokens on the specified writer.
 *
 * @author davins
 */
public class JsonWriter implements AutoCloseable
{

	private final Writer writer;

	/**
	 * Creates a new JsonWriter for the specified Writer.
	 *
	 * @param writer the writer where JSON tokens will be written
	 */
	public JsonWriter(Writer writer)
	{
		this.writer = writer;
	}

	/**
	 * Writes the specified JsonToken on the associated writer.
	 *
	 * @param type type of the token to be written
	 * @param value value of the token to be written
	 *
	 * @throws gate.error.ConversionException if an error occurs white
	 * trying to write the token
	 */
	public void write(JsonToken.Type type, String value)
		throws ConversionException
	{
		Objects.requireNonNull(type);
		try
		{
			switch (type)
			{
				case OPEN_ARRAY:
					writer.write("[ ");
					break;
				case CLOSE_ARRAY:
					writer.write(" ]");
					break;
				case OPEN_OBJECT:
					writer.write("{ ");
					break;
				case CLOSE_OBJECT:
					writer.write(" }");
					break;
				case DOUBLE_DOT:
					writer.write(':');
					break;
				case TRUE:
					writer.write("true");
					break;
				case FALSE:
					writer.write("false");
					break;
				case COMMA:
					writer.write(',');
					break;
				case NULL:
					writer.write("null");
					break;
				case EOF:
					break;
				case NUMBER:
					Objects.requireNonNull(value, "Number not specified");
					writer.write(value);
					break;
				case STRING:
					Objects.requireNonNull(value, "String not specified");
					writer.write("\"");
					for (int i = 0; i < value.length(); i++)
					{
						if (value.charAt(i) == '"')
							writer.write('\\');
						writer.write(value.charAt(i));
					}
					writer.write("\"");
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage());
		}

	}

	@Override
	public void close()
	{
		try
		{
			writer.close();
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}
}
