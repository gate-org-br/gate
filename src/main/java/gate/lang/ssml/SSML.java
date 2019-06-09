package gate.lang.ssml;

/**
 * SSML generator
 */
public class SSML
{

	private int index = 0;
	private final String source;
	private final StringBuilder target = new StringBuilder();

	private final static String DOT = ".<break strength='strong'/>";
	private final static String COMMA = ",<break strength='weak'/>";
	private final static String DOUBLE_DOT = ":<break strength='x-strong'/>";
	private final static String SEMICOLON = ";<break strength='medium'/>";
	private final static String QUESTION_MARK = "?<break strength='medium'/>";

	private SSML(String source)
	{
		this.source = source;
	}

	private String process()
	{
		target.append("<speak>");
		while (current() != -1)
			next();
		target.append("</speak>");
		return target.toString();
	}

	private int consume()
	{
		if (index == source.length())
			return -1;
		return source.charAt(index++);
	}

	private int current()
	{
		if (index == source.length())
			return -1;
		return source.charAt(index);
	}

	private void next()
	{
		if (Character.isSpaceChar(current()))
			space();
		else if (current() == '<')
			tag();
		else if (current() == '.')
			pause(DOT);
		else if (current() == ',')
			pause(COMMA);
		else if (current() == ':')
			pause(DOUBLE_DOT);
		else if (current() == ';')
			pause(SEMICOLON);
		else if (current() == '?')
			pause(QUESTION_MARK);
		else if (current() != -1)
			target.append((char) consume());
	}

	private void space()
	{
		target.append(' ');
		while (current() != -1 && Character.isSpaceChar(current()))
			consume();
	}

	private void pause(String string)
	{
		target.append(string);
		consume();
	}

	private void doubleQuotedString()
	{
		target.append((char) consume());
		while (current() != -1 && current() != '"')
			target.append((char) consume());
		if (current() != -1)
			target.append((char) consume());
	}

	private void singleQuotedString()
	{
		target.append((char) consume());
		while (current() != -1 && current() != '\'')
			target.append((char) consume());
		if (current() != -1)
			target.append((char) consume());
	}

	private void tag()
	{
		target.append((char) consume());
		while (current() != -1 && current() != '>')
		{
			if (current() == '"')
				doubleQuotedString();
			if (current() == '\'')
				singleQuotedString();
			target.append((char) consume());
		}
		if (current() != -1)
			target.append((char) consume());
	}

	/**
	 * Creates SSML markup text from the specified source string with pauses added to it's punctuation marks.
	 *
	 * @param source the string to be converted to SSML markup text
	 * @return the SSML text markup text generated from the specified string
	 */
	public static String generate(String source)
	{
		return new SSML(source).process();
	}
}
