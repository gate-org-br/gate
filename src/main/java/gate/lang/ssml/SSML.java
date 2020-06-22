package gate.lang.ssml;

/**
 * SSML generator
 */
public class SSML
{

	private int index = 0;
	private final String source;
	private final StringBuilder target = new StringBuilder();

	private final static String MEDIUM = "<break strength='medium'/>";
	private final static String STRONG = "<break strength='strong'/>";
	private final static String XSTRONG = "<break strength='x-strong'/>";

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
			dot();
		else if (current() == ',')
			comma();
		else if (current() == ':')
			doubleDot();
		else if (current() == ';')
			semicolon();
		else if (current() == '?')
			questionMark();
		else if (current() != -1)
			target.append((char) consume());
	}

	private void dot()
	{
		consume();
		target.append(".");
		if (current() == '\n'
			|| current() == '\r')
			paragraph();
		else
			target.append(STRONG);
	}

	private void comma()
	{
		consume();
		target.append(",");
		target.append(MEDIUM);
	}

	private void semicolon()
	{
		consume();
		target.append(";");
		target.append(MEDIUM);
	}

	private void questionMark()
	{
		consume();
		target.append("?");
		target.append(MEDIUM);
	}

	private void doubleDot()
	{
		consume();
		target.append(":");
		target.append(XSTRONG);
	}

	private void space()
	{
		target.append(' ');
		while (current() != -1 && Character.isSpaceChar(current()))
			consume();
	}

	public void paragraph()
	{
		if (current() == '\r')
		{
			consume();
			target.append("\r");

			if (current() == '\n')
			{
				consume();
				target.append("\n");
			}

		} else if (current() == '\n')
		{
			consume();
			target.append("\n");
		}

		target.append(XSTRONG);
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
	 * Creates SSML markup text from the specified source string with pauses
	 * added to it's punctuation marks.
	 *
	 * @param source the string to be converted to SSML markup text
	 * @return the SSML text markup text generated from the specified string
	 */
	public static String generate(String source)
	{
		return new SSML(source).process();
	}
}
