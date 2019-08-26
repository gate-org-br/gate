package gate.lang.HTMLCleaner;

import gate.error.TemplateException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class Scanner extends BufferedReader
{

	public Scanner(Reader reader)
	{
		super(reader);
	}

	public Object next() throws TemplateException
	{
		try
		{
			if (isEOF())
				return Token.EOF;

			if (consume("\\"))
				return isEOF() ? Token.EOF
					: new Char((char) read());

			if (consume("&nbsp;"))
				return new Char(' ');
			if (consume("&aacute;"))
				return new Char('á');
			if (consume("&eacute;"))
				return new Char('é');
			if (consume("&iacute;"))
				return new Char('í');
			if (consume("&oacute;"))
				return new Char('ó');
			if (consume("&uacute;"))
				return new Char('ú');
			if (consume("&Aacute;"))
				return new Char('Á');
			if (consume("&Eacute;"))
				return new Char('É');
			if (consume("&Iacute;"))
				return new Char('Í');
			if (consume("&Oacute;"))
				return new Char('Ó');
			if (consume("&Uacute;"))
				return new Char('Ú');

			if (consume("&acirc;"))
				return new Char('â');
			if (consume("&ecirc;"))
				return new Char('ê');
			if (consume("&icirc;"))
				return new Char('î');
			if (consume("&ocirc;"))
				return new Char('ô');
			if (consume("&ucirc;"))
				return new Char('û');
			if (consume("&Acirc;"))
				return new Char('Â');
			if (consume("&Ecirc;"))
				return new Char('Ê');
			if (consume("&Icirc;"))
				return new Char('Î');
			if (consume("&Ocirc;"))
				return new Char('Û');
			if (consume("&Ucirc;"))
				return new Char('Û');

			if (consume("&atilde;"))
				return new Char('ã');
			if (consume("&etilde;"))
				return new Char('ẽ');
			if (consume("&itilde;"))
				return new Char('ĩ');
			if (consume("&otilde;"))
				return new Char('õ');
			if (consume("&utilde;"))
				return new Char('ũ');
			if (consume("&Atilde;"))
				return new Char('Ã');
			if (consume("&Etilde;"))
				return new Char('Ẽ');
			if (consume("&Itilde;"))
				return new Char('Ĩ');
			if (consume("&Otilde;"))
				return new Char('Õ');
			if (consume("&Utilde;"))
				return new Char('Ũ');

			if (consume("&agrave;"))
				return new Char('à');
			if (consume("&egrave;"))
				return new Char('è');
			if (consume("&igrave;"))
				return new Char('ì');
			if (consume("&ograve;"))
				return new Char('ò');
			if (consume("&ugrave;"))
				return new Char('ù');
			if (consume("&Agrave;"))
				return new Char('À');
			if (consume("&Egrave;"))
				return new Char('È');
			if (consume("&Igrave;"))
				return new Char('Ì');
			if (consume("&Ograve;"))
				return new Char('Ò');
			if (consume("&Ugrave;"))
				return new Char('Ù');

			if (consume("&ccedil;"))
				return new Char('ç');
			if (consume("&Ccedil;"))
				return new Char('Ç');

			if (consume(Token.OPEN_TAG.toString()))
				return Token.OPEN_TAG;

			if (consume(Token.CLOSE_TAG.toString()))
				return Token.CLOSE_TAG;

			if (consume(Token.QUOTE.toString()))
				return Token.QUOTE;
			if (consume(Token.DOUBLE_QUOTE.toString()))
				return Token.DOUBLE_QUOTE;

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
