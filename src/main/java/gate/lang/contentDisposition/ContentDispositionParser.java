package gate.lang.contentDisposition;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
		if (!(current instanceof String string))
			throw new ParseException("expected value and found " + current, 0);

		current = scanner.scan();
		while (Character.valueOf(';').equals(current))
		{
			current = scanner.scan();

			if (!(current instanceof String name))
				throw new ParseException("expected parameter and found " + current, 0);
			current = scanner.scan();

			if (!Character.valueOf('=').equals(current))
				throw new ParseException("expected = and found " + current, 0);
			current = scanner.scan();

			if (!(current instanceof String value))
				throw new ParseException("expected value and found " + current, 0);
			current = scanner.scan();

			parameters.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8));
		}

		return new ContentDisposition(string, parameters);
	}

	@Override
	public void close()
	{
		scanner.close();
	}
}
