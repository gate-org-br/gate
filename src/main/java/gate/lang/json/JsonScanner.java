package gate.lang.json;

import gate.error.AppError;
import gate.error.ConversionException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Scans a source for JSON tokens.
 */
public final class JsonScanner implements AutoCloseable
{

	private int c = -1;
	private JsonToken current;
	private final Reader reader;
	private final StringBuilder string = new StringBuilder();

	public JsonScanner(Reader reader) throws ConversionException
	{
		this.reader = reader;
		scan();
	}

	/**
	 * Creates a JSONScanner for a string.
	 *
	 * @param string the string to be scanned for JSON tokens
	 *
	 * @throws gate.error.ConversionException if any Exception occurs while
	 * parsing the string
	 */
	public JsonScanner(String string) throws ConversionException
	{
		this(new StringReader(string));
	}

	/**
	 * Gets the last JSON token scanned.
	 *
	 * @return the last JSON token scanned
	 */
	public JsonToken getCurrent()
	{
		return current;
	}

	/**
	 * Scans the source for the next JSON token.
	 *
	 * @return the next JSON token scanned
	 *
	 * @throws gate.error.ConversionException if any Exception occurs while
	 * parsing the string
	 */
	public JsonToken scan() throws ConversionException
	{
		try
		{
			if (c == -1)
				c = reader.read();

			while (Character.isWhitespace(c))
				c = reader.read();

			if (c == -1)
				return current = JsonToken.EOF;

			switch (c)
			{
				case ':':
					c = reader.read();
					return current = JsonToken.DOUBLE_DOT;
				case ',':
					c = reader.read();
					return current = JsonToken.COMMA;
				case '{':
					c = reader.read();
					return current = JsonToken.OPEN_OBJECT;
				case '}':
					c = reader.read();
					return current = JsonToken.CLOSE_OBJECT;
				case '[':
					c = reader.read();
					return current = JsonToken.OPEN_ARRAY;
				case ']':
					c = reader.read();
					return current = JsonToken.CLOSE_ARRAY;
				case '"':
				case '\'':
					int delimiter = c;
					string.setLength(0);
					for (c = reader.read(); c != delimiter; c = reader.read())
					{
						if (c == -1)
							throw new ConversionException("Expected char and found EOF");

						if (c == '\\')
						{
							c = reader.read();
							if (c == -1)
								throw new ConversionException("Expected char and found EOF");
						}

						string.append((char) c);
					}

					c = reader.read();
					return current = new JsonToken(JsonToken.Type.STRING, string.toString());

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '+':
				case '-':

					string.setLength(0);

					do
					{
						string.append((char) c);
						c = reader.read();
					} while (Character.isDigit(c));

					if (c == '.')
					{
						string.append((char) c);

						c = reader.read();

						if (!Character.isDigit(c))
							throw new ConversionException("Expected digit and found " + (char) c);

						string.append((char) c);

						c = reader.read();

						while (Character.isDigit(c))
						{
							string.append((char) c);
							c = reader.read();
						}
					}

					if (c == 'e' || c == 'E')
					{
						string.append((char) 'e');
						c = reader.read();

						if (c == '+' || c == '-')
						{
							string.append((char) c);
							c = reader.read();
						}

						if (!Character.isDigit(c))
							throw new ConversionException("Expected digit and found " + (char) c);

						do
						{
							string.append((char) c);
							c = reader.read();
						} while (Character.isDigit(c));
					}

					return current = new JsonToken(JsonToken.Type.NUMBER, string.toString());

				default:

					if (!Character.isJavaIdentifierStart(c))
						throw new ConversionException("Expected identifier and found " + (char) c);

					string.setLength(0);

					while (Character.isJavaIdentifierPart(c))
					{
						string.append((char) c);
						c = reader.read();
					}

					String name = string.toString();

					switch (name)
					{
						case "true":
							return current = JsonToken.TRUE;

						case "false":
							return current = JsonToken.FALSE;

						case "null":
							return current = JsonToken.NULL;

						default:
							return current = new JsonToken(JsonToken.Type.STRING, name);
					}
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
			reader.close();
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}
}
