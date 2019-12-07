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

	public TemplateScanner skipSpaces() throws TemplateException

	{
		try
		{
			do
			{
				mark(1);
			} while (Character.isWhitespace(read()));
			reset();
			return this;
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	public Evaluable next() throws TemplateException
	{
		try
		{
			if (isEOF())
				return TemplateToken.EOF;

			if (consume("\\"))
				return isEOF() ? TemplateToken.EOF
					: new Char((char) read());

			if (consume(TemplateToken.COMPLEX_LINE_BREAK.toString()))
				return TemplateToken.COMPLEX_LINE_BREAK;
			if (consume(TemplateToken.SIMPLE_LINE_BREAK.toString()))
				return TemplateToken.SIMPLE_LINE_BREAK;
			if (consume(TemplateToken.SPACE.toString()))
				return TemplateToken.SPACE;
			if (consume(TemplateToken.TAB.toString()))
				return TemplateToken.TAB;

			if (consume(TemplateToken.EXPRESSION_HEAD.toString()))
				return TemplateToken.EXPRESSION_HEAD;

			if (consume(TemplateToken.IMPORT.toString()))
				return TemplateToken.IMPORT;

			if (consume(TemplateToken.IF_HEAD.toString()))
				return TemplateToken.IF_HEAD;

			if (consume(TemplateToken.ITERATOR_HEAD.toString()))
				return TemplateToken.ITERATOR_HEAD;

			if (consume(TemplateToken.CONDITION.toString()))
				return TemplateToken.CONDITION;
			if (consume(TemplateToken.SOURCE.toString()))
				return TemplateToken.SOURCE;
			if (consume(TemplateToken.TARGET.toString()))
				return TemplateToken.TARGET;
			if (consume(TemplateToken.TYPE.toString()))
				return TemplateToken.TYPE;
			if (consume(TemplateToken.RESOURCE.toString()))
				return TemplateToken.RESOURCE;
			if (consume(TemplateToken.INDEX.toString()))
				return TemplateToken.INDEX;
			if (consume(TemplateToken.EQUALS.toString()))
				return TemplateToken.EQUALS;
			if (consume(TemplateToken.DOUBLE_QUOTE.toString()))
				return TemplateToken.DOUBLE_QUOTE;
			if (consume(TemplateToken.QUOTE.toString()))
				return TemplateToken.QUOTE;

			if (consume(TemplateToken.SELF_CLOSE_TAG.toString()))
				return TemplateToken.SELF_CLOSE_TAG;

			if (consume(TemplateToken.IMPORT.toString()))
				return TemplateToken.IMPORT;

			if (consume(TemplateToken.IF_TAIL.toString()))
				return TemplateToken.IF_TAIL;

			if (consume(TemplateToken.ITERATOR_TAIL.toString()))
				return TemplateToken.ITERATOR_TAIL;

			if (consume(TemplateToken.EXPRESSION_TAIL.toString()))
				return TemplateToken.EXPRESSION_TAIL;

			if (consume(TemplateToken.CLOSE_TAG.toString()))
				return TemplateToken.CLOSE_TAG;
			if (consume(TemplateToken.DOT.toString()))
				return TemplateToken.DOT;
			if (consume(TemplateToken.OPEN_PARENTESIS.toString()))
				return TemplateToken.OPEN_PARENTESIS;
			if (consume(TemplateToken.CLOSE_PARENTESIS.toString()))
				return TemplateToken.CLOSE_PARENTESIS;

			return new Char((char) read());
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}

	private boolean isEOF() throws IOException
	{
		mark(1);
		int c = read();
		reset();
		return c == -1;
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
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}
}
