package gate.lang.xml;

import gate.error.TemplateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class XMLScanner extends BufferedReader
{

	public XMLScanner(Reader reader)
	{
		super(reader);
	}

	public XMLEvaluable next() throws TemplateException
	{
		try
		{
			if (isEOF())
				return XMLToken.EOF;

			if (consume("\\"))
				return isEOF() ? XMLToken.EOF
					: XMLChar.of((char) read());

			for (XMLEntity entity : XMLEntity.VALUES)
				if (consume(entity.toString()))
					return entity;

			if (consume(XMLToken.OPEN_TAG.toString()))
				return XMLToken.OPEN_TAG;

			if (consume(XMLToken.CLOSE_TAG.toString()))
				return XMLToken.CLOSE_TAG;

			if (consume(XMLToken.QUOTE.toString()))
				return XMLToken.QUOTE;
			if (consume(XMLToken.DOUBLE_QUOTE.toString()))
				return XMLToken.DOUBLE_QUOTE;

			return XMLChar.of((char) read());
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
