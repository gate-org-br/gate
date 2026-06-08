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
	 * @param type  type of the token to be written
	 * @param value value of the token to be written
	 * @throws gate.error.ConversionException if an error occurs white trying to write the token
	 */
	public void write(JsonToken.Type type, String value)
			throws ConversionException
	{
		Objects.requireNonNull(type);
		try
		{
			switch (type)
			{
				case OPEN_ARRAY -> writer.write('[');
				case CLOSE_ARRAY -> writer.write(']');
				case OPEN_OBJECT -> writer.write('{');
				case CLOSE_OBJECT -> writer.write('}');
				case DOUBLE_DOT -> writer.write(':');
				case TRUE -> writer.write("true");
				case FALSE -> writer.write("false");
				case COMMA -> writer.write(',');
				case NULL -> writer.write("null");
				case EOF -> {}
				case NUMBER ->
				{
					Objects.requireNonNull(value, "Number not specified");
					writer.write(value);
				}
				case STRING ->
				{
					Objects.requireNonNull(value, "String not specified");
					writer.write('"');
					for (int i = 0; i < value.length(); i++)
					{
						switch (value.charAt(i))
						{
							case '\n' -> writer.write("\\n");
							case '\r' -> writer.write("\\r");
							case '\t' -> writer.write("\\t");
							case '\b' -> writer.write("\\b");
							case '\f' -> writer.write("\\f");
							case '\\' -> writer.write("\\\\");
							case '\"' -> writer.write("\\\"");
							default -> writer.write((char) value.charAt(i));
						}
					}
					writer.write('"');
				}
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage());
		}

	}

	public void write(String value)
			throws ConversionException
	{
		try
		{
			writer.write(value);
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