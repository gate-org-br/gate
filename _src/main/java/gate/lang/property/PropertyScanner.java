package gate.lang.property;

import gate.error.PropertyError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class PropertyScanner extends BufferedReader
{

	private int c = -1;

	public PropertyScanner(String string)
	{
		super(new StringReader(string));
	}

	public Object next()
	{
		try
		{
			if (c == -1)
				c = read();
			while (Character.isWhitespace(c))
				c = read();
			StringBuilder string = new StringBuilder();

			switch (c)
			{
				case -1:
					return null;

				case '+':
				case '-':
				case '!':
				case '=':
				case '>':
				case '<':
				case '%':
				case '@':
				case '#':
				case '^':
					int m = c;
					c = read();
					return (char) m;

				case '.':
				case ',':
				case '[':
				case ']':
				case '(':
				case ')':
					int t = c;
					c = read();
					return (char) t;

				case '"':
					string.setLength(0);
					for (c = read(); c != '"'; c = read())
						if (c != -1)
							string.append(c == '\\' ? (char) read() : (char) c);
						else
							throw new PropertyError("String não terminada.");
					c = read();
					return string.toString();

				case '\'':
					string.setLength(0);
					for (c = read(); c != '\''; c = read())
						if (c != -1)
							string.append(c == '\\' ? (char) read() : (char) c);
						else
							throw new PropertyError("String não terminada.");
					c = read();
					return string.toString();

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

					string.setLength(0);
					string.append((char) c);
					for (c = read(); Character.isDigit(c); c = read())
						string.append((char) c);

					if (c == '.')
					{
						string.append((char) c);
						for (c = read(); Character.isDigit(c); c = read())
							string.append((char) c);
						return Double.valueOf(string.toString());
					}

					return Integer.valueOf(string.toString());

				default:
					if (!Character.isJavaIdentifierStart(c))
						throw new PropertyError("Invalid attribute name.");

					string.setLength(0);
					string.append((char) c);
					for (c = read(); Character.isJavaIdentifierPart(c); c = read())
						string.append((char) c);
					String result = string.toString();

					switch (result)
					{
						case "true":
							return Boolean.TRUE;
						case "false":
							return Boolean.FALSE;
						default:
							return result;
					}

			}
		} catch (IOException e)
		{
			throw new PropertyError(String.format("Error on trying to parse property: %s", e.getMessage()));
		}
	}
}
