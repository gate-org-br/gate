package gate.lang.template;

import gate.error.TemplateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class TemplateScanner extends BufferedReader
{

	public TemplateScanner(Reader reader)
	{
		super(reader);
	}

	private boolean isEOF()
			throws IOException
	{
		mark(1);
		if (read() == -1)
			return true;
		reset();
		return false;
	}

	private void skipSpaces() throws TemplateException

	{
		try
		{
			do
			{
				mark(1);
			} while (Character.isWhitespace(read()));
			reset();
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}

	private boolean check(String string) throws TemplateException
	{
		try
		{
			mark(string.length());

			for (int i = 0; i < string.length(); i++)
			{
				if (string.charAt(i) != read())
				{
					reset();
					return false;
				}
			}

			reset();
			return true;
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}

	private String consumeName() throws IOException
	{
		mark(1);
		int c = read();
		if (Character.isJavaIdentifierStart(c))
		{
			StringBuilder string = new StringBuilder();
			while (Character.isJavaIdentifierPart(c))
			{
				string.append((char) c);
				mark(1);
				c = read();
			}

			reset();
			return string.toString();
		}
		reset();
		return null;
	}

	private boolean consume(String string) throws TemplateException
	{
		try
		{
			mark(string.length());

			for (int i = 0; i < string.length(); i++)
			{
				if (string.charAt(i) != read())
				{
					reset();
					return false;
				}
			}

			return true;
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}

	public TemplateToken nextTplToken() throws TemplateException
	{
		try
		{
			if (isEOF()
					|| check("</g-if>")
					|| check("</g-for>"))
				return new TemplateToken(TemplateToken.Type.EOF, "");

			if (consume("${"))
				return new TemplateToken(TemplateToken.Type.EXPRESSION_HEAD, "${");
			if (consume("<g-if"))
				return new TemplateToken(TemplateToken.Type.IF_HEAD, "<g-if");
			if (consume("<g-for"))
				return new TemplateToken(TemplateToken.Type.FOR_HEAD, "<g-for");

			StringBuilder string = new StringBuilder();
			while (!isEOF()
					&& !check("${")
					&& !check("<g-if")
					&& !check("<g-for")
					&& !check("</g-if>")
					&& !check("</g-for>"))
				string.append((char) read());
			return new TemplateToken(TemplateToken.Type.TEXT, string.toString());
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}

	public TemplateToken nextExpToken() throws TemplateException
	{
		try
		{
			skipSpaces();

			if (isEOF())
				throw new TemplateException("Unexpected end of file.");

			if (consume("}"))
				return new TemplateToken(TemplateToken.Type.EXPRESSION_TAIL, "}");

			StringBuilder string = new StringBuilder();
			while (!check("}"))
			{
				int c = read();
				if (c == '"')
				{
					string.append((char) c);
					for (c = read(); c != '"'; c = read())
						string.append((char) c);
				}

				if (c == '\'')
				{
					string.append((char) c);
					for (c = read(); c != '\''; c = read())
						string.append((char) c);
				}

				string.append((char) c);
			}
			return new TemplateToken(TemplateToken.Type.TEXT, string.toString());
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}

	public TemplateToken nextTagToken() throws TemplateException
	{
		try
		{
			skipSpaces();

			if (consume("condition"))
				return new TemplateToken(TemplateToken.Type.CONDITION, "condition");
			if (consume("each"))
				return new TemplateToken(TemplateToken.Type.EACH, "each");
			if (consume("name"))
				return new TemplateToken(TemplateToken.Type.NAME, "name");
			if (consume("="))
				return new TemplateToken(TemplateToken.Type.EQUALS, "=");
			if (consume("\""))
				return new TemplateToken(TemplateToken.Type.DOUBLE_QUOTE, "\"");
			if (consume("\'"))
				return new TemplateToken(TemplateToken.Type.QUOTE, "\"");
			if (consume("${"))
				return new TemplateToken(TemplateToken.Type.EXPRESSION_HEAD, "${");
			if (consume(">"))
				return new TemplateToken(TemplateToken.Type.CLOSE_TAG, ">");
			if (consume("</g-if>"))
				return new TemplateToken(TemplateToken.Type.IF_TAIL, "</g-if>");
			if (consume("</g-for>"))
				return new TemplateToken(TemplateToken.Type.FOR_TAIL, "</g-for>");

			String string = consumeName();
			if (string != null)
				return new TemplateToken(TemplateToken.Type.NAME, string);

			throw new TemplateException("Unexpected character found : %c", (char) read());
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}
}
