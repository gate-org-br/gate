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

	private String identation() throws TemplateException
	{
		try
		{

			mark(2);
			if (read() == '\r' && read() == '\n')
			{
				StringBuilder string = new StringBuilder("\r\n");

				mark(1);
				int c = read();
				while (c == ' ' || c == '\t')
				{
					string.append((char) c);
					mark(1);
					c = read();
				}

				reset();
				return string.toString();
			}
			reset();

			mark(1);
			if (read() == '\n')
			{
				StringBuilder string = new StringBuilder("\n");

				mark(1);
				int c = read();
				while (c == ' ' || c == '\t')
				{
					string.append((char) c);
					mark(1);
					c = read();
				}

				reset();
				return string.toString();
			}
			reset();
			return "";

		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
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

	private String consumeTarget() throws IOException
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
			if (isEOF())
				return TemplateToken.EOF;

			if (consume(TemplateToken.EXPRESSION_HEAD.getValue()))
				return TemplateToken.EXPRESSION_HEAD;

			String identation = identation();

			if (check(TemplateToken.IF_TAIL.getValue())
				|| check(TemplateToken.ITERATOR_TAIL.getValue()))
				return TemplateToken.EOF;

			if (consume(TemplateToken.IF_HEAD.getValue()))
				return TemplateToken.IF_HEAD;

			if (consume(TemplateToken.ITERATOR_HEAD.getValue()))
				return TemplateToken.ITERATOR_HEAD;

			StringBuilder string = new StringBuilder(identation);
			while (!isEOF()
				&& !check("\n")
				&& !check(TemplateToken.EXPRESSION_HEAD.getValue())
				&& !check(TemplateToken.IF_HEAD.getValue())
				&& !check(TemplateToken.ITERATOR_HEAD.getValue())
				&& !check(TemplateToken.IF_TAIL.getValue())
				&& !check(TemplateToken.ITERATOR_TAIL.getValue()))
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

			if (consume(TemplateToken.EXPRESSION_TAIL.getValue()))
				return TemplateToken.EXPRESSION_TAIL;

			StringBuilder string = new StringBuilder();
			while (!check(TemplateToken.EXPRESSION_TAIL.getValue()))
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

			if (consume(TemplateToken.CONDITION.getValue()))
				return TemplateToken.CONDITION;
			if (consume(TemplateToken.SOURCE.getValue()))
				return TemplateToken.SOURCE;
			if (consume(TemplateToken.TARGET.getValue()))
				return TemplateToken.TARGET;
			if (consume(TemplateToken.INDEX.getValue()))
				return TemplateToken.INDEX;
			if (consume(TemplateToken.EQUALS.getValue()))
				return TemplateToken.EQUALS;
			if (consume(TemplateToken.DOUBLE_QUOTE.getValue()))
				return TemplateToken.DOUBLE_QUOTE;
			if (consume(TemplateToken.QUOTE.getValue()))
				return TemplateToken.QUOTE;
			if (consume(TemplateToken.EXPRESSION_HEAD.getValue()))
				return TemplateToken.EXPRESSION_HEAD;

			if (consume(TemplateToken.CLOSE_TAG.getValue()))
				return TemplateToken.CLOSE_TAG;

			if (consume(TemplateToken.IF_TAIL.getValue()))
				return TemplateToken.IF_TAIL;

			if (consume(TemplateToken.ITERATOR_TAIL.getValue()))
				return TemplateToken.ITERATOR_TAIL;

			String string = consumeTarget();
			if (string != null)
				return new TemplateToken(TemplateToken.Type.TARGET, string);

			throw new TemplateException(String.format("Unexpected character found : %c", (char) read()));
		} catch (IOException e)
		{
			throw new TemplateException("Error trying to parse template.");
		}
	}
}
