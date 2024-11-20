package gate.lang.template;

import gate.error.TemplateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class TemplateScanner extends BufferedReader
{

	private int c = Integer.MAX_VALUE;

	public TemplateScanner(Reader reader)
	{
		super(reader);
	}

	public Evaluable next() throws TemplateException
	{
		try
		{
			if (c == Integer.MAX_VALUE)
				c = read();
			StringBuilder token = new StringBuilder();

			if (c == -1)
				return TemplateToken.EOF;

			if (c == '\n')
			{
				mark(3);
				if (read() == '{'
						&& read() == '{'
						&& read() == '/')
				{
					c = read();
					return TemplateToken.BLOCK_END;
				}

				reset();
			}

			if (c == '{')
			{
				if (read() == '{')
				{
					c = read();
					switch (c)
					{
						case '#':
							c = read();
							return TemplateToken.BLOCK;
						case '^':
							c = read();
							return TemplateToken.INVERTED_BLOCK;
						case '/':
							c = read();
							return TemplateToken.BLOCK_END;
						default:
							return TemplateToken.EXPRESSION_HEAD;
					}
				}
				return new Char('{');
			}

			if (c == '}')
			{
				c = read();
				if (c == '}')
				{
					c = read();
					return TemplateToken.EXPRESSION_TAIL;
				}
				return TemplateToken.EL_TAIL;
			}
			
			if (c == '$')
			{
				c = read();
				if (c == '{')
				{
					c = read();
					return TemplateToken.EL_HEAD;
				}
				return new Char('$');
			}

			token.append((char) c);
			for (c = read();
					c != -1
					&& c != '{'
					&& c != '}'; c = read())
				token.append((char) c);
			return new Text(token.toString());
		} catch (IOException ex)
		{
			throw new TemplateException(ex, "Error trying to parse template.");
		}
	}
}
