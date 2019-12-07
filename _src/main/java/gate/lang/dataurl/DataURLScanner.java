package gate.lang.dataurl;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

public class DataURLScanner implements AutoCloseable
{

	private int c = Integer.MAX_VALUE;
	private final Reader reader;
	private final StringBuilder string
		= new StringBuilder();

	public DataURLScanner(Reader reader)
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
		switch (c)
		{
			case -1:
				return null;
			case ':':
				c = reader.read();
				return ':';
			case ';':
				c = reader.read();
				return ';';
			case ',':
				c = reader.read();
				return ',';
			case '=':
				c = reader.read();
				return '=';
			case '/':
				c = reader.read();
				return '/';
			default:
				string.setLength(0);
				string.append((char) c);
				for (c = reader.read();
					c != -1
					&& c != ':'
					&& c != '/'
					&& c != ';'
					&& c != '='
					&& c != ',';
					c = reader.read())
					string.append((char) c);
				return string.toString();
		}
	}

	public String finish() throws IOException
	{
		string.setLength(0);
		string.append((char) c);
		for (c = reader.read();
			c != -1;
			c = reader.read())
			string.append((char) c);
		return string.toString();
	}

}
