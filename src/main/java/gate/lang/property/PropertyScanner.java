package gate.lang.property;

import gate.error.PropertyError;

public class PropertyScanner
{

	private final String input;
	private int position = 0;

	public PropertyScanner(String input)
	{
		this.input = input;
	}

	private int peek()
	{
		return position < input.length()
			? input.charAt(position)
			: -1;
	}

	private int read()
	{
		return position < input.length()
			? input.charAt(position++)
			: -1;
	}

	public Object next()
	{
		while (position < input.length()
			&& Character.isWhitespace(input.charAt(position)))
			position++;

		int c = peek();
		if (c == -1)
			return null;

		return switch (c)
		{
			// Delimiters
			case '.', ',', '[', ']', '(', ')' ->
			{
				read();
				yield (char) c;
			}

			// Operators
			case '+', '-', '!', '=', '>', '<', '%', '@', '#', '^' ->
			{
				read();
				yield (char) c;
			}

			// Strings
			case '"', '\'' ->
				readString((char) c);

			// Numbers
			case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
				readNumber();

			// Identifiers or error
			default ->
			{
				if (Character.isJavaIdentifierStart(c))
					yield readIdentifier();

				throw new PropertyError("Unexpected character: " + (char) c);
			}
		};
	}

	private String readString(char delimiter)
	{
		read(); // Consume opening quote
		StringBuilder sb = new StringBuilder();

		while (peek() != delimiter)
		{
			int c = read();
			if (c == -1)
				throw new PropertyError("Unterminated string");

			if (c == '\\')
				sb.append(readEscape());
			else
				sb.append((char) c);
		}

		read(); // Consume closing quote
		return sb.toString();
	}

	private char readEscape()
	{
		int c = read();
		return switch (c)
		{
			case 'n' ->
				'\n';
			case 't' ->
				'\t';
			case 'r' ->
				'\r';
			case '\\' ->
				'\\';
			case '"' ->
				'"';
			case '\'' ->
				'\'';
			default ->
				throw new PropertyError("Invalid escape: \\" + (char) c);
		};
	}

	private Number readNumber()
	{
		StringBuilder sb = new StringBuilder();

		// Negative sign
		if (peek() == '-')
		{
			sb.append((char) read());

			// Must have at least one digit after '-'
			if (!Character.isDigit(peek()))
				throw new PropertyError("Invalid number: '-' must be followed by digits");
		}

		// Digits before decimal point
		while (Character.isDigit(peek()))
			sb.append((char) read());

		// Decimal part
		if (peek() == '.')
		{
			sb.append((char) read());
			while (Character.isDigit(peek()))
				sb.append((char) read());

			return Double.valueOf(sb.toString());
		}

		return Integer.valueOf(sb.toString());
	}

	private Object readIdentifier()
	{
		StringBuilder sb = new StringBuilder();
		sb.append((char) read());

		while (Character.isJavaIdentifierPart(peek()))
			sb.append((char) read());

		String result = sb.toString();

		// Convert boolean literals
		if (result.equals("true"))
			return Boolean.TRUE;
		if (result.equals("false"))
			return Boolean.FALSE;

		return result;
	}
}
