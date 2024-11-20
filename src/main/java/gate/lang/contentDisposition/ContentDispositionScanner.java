package gate.lang.contentDisposition;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

public class ContentDispositionScanner implements AutoCloseable
{

	private int c = Integer.MAX_VALUE;
	private final Reader reader;

	public ContentDispositionScanner(Reader reader)
	{
		this.reader = reader;
	}

	@Override
	public void close()
	{
		try
		{
			reader.close();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public Object scan() throws IOException
	{
		if (c == Integer.MAX_VALUE)
			c = reader.read();
		return switch (c)
		{
			case -1 ->
				null;
			case ';' ->
			{
				c = reader.read();
				yield ';';
			}
			case '=' ->
			{
				c = reader.read();
				yield '=';
			}

			case ' ' ->
			{
				c = reader.read();
				yield ' ';
			}
			case '"' ->
			{
				StringBuilder string
						= new StringBuilder();
				for (c = reader.read();
						c != -1
						&& c != '"';
						c = reader.read())
					string.append((char) c);
				if (c == '"')
					c = reader.read();
				else
					throw new IOException("Invalid header parameter value");
				yield string.toString();
			}
			default ->
			{
				StringBuilder string
						= new StringBuilder();
				string.append((char) c);
				for (c = reader.read();
						c != -1
						&& c != ':'
						&& c != ';'
						&& c != '='
						&& c != ','
						&& c != ' ';
						c = reader.read())
					string.append((char) c);
				yield string.toString();
			}
		};
	}

	public String finish() throws IOException
	{
		StringBuilder string
				= new StringBuilder();
		string.setLength(0);
		string.append((char) c);
		for (c = reader.read();
				c != -1;
				c = reader.read())
			string.append((char) c);
		return string.toString();
	}

}
