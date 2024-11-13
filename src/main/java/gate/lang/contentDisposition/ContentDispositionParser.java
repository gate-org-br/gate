package gate.lang.contentDisposition;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContentDispositionParser implements AutoCloseable
{

	private final ContentDispositionScanner scanner;

	public ContentDispositionParser(ContentDispositionScanner scanner)
	{
		this.scanner = scanner;
	}

	public ContentDisposition parse() throws IOException, ParseException
	{

		Map<String, String> parameters = new LinkedHashMap<>();

		Object current = scanner.scan();
		if (!(current instanceof String))
			throw new ParseException("expected value and found " + current, 0);
		String string = (String) current;

		current = scanner.scan();
		while (Character.valueOf(' ').equals(current))
			current = scanner.scan();
		while (Character.valueOf(';').equals(current))
		{
			current = scanner.scan();
			while (Character.valueOf(' ').equals(current))
				current = scanner.scan();

			if (!(current instanceof String))
				throw new ParseException("expected parameter and found " + current, 0);
			String name = (String) current;
			current = scanner.scan();

			if (!Character.valueOf('=').equals(current))
				throw new ParseException("expected = and found " + current, 0);
			current = scanner.scan();

			if (!(current instanceof String))
				throw new ParseException("expected value and found " + current, 0);
			String value = (String) current;
			current = scanner.scan();

			parameters.put(name, URLDecoder.decode(value, "UTF-8"));

			while (Character.valueOf(' ').equals(current))
				current = scanner.scan();
		}

		return new ContentDisposition(string, parameters);
	}

	@Override
	public void close()
	{
		scanner.close();
	}
}
