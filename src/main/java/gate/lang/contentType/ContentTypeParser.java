package gate.lang.contentType;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContentTypeParser implements AutoCloseable
{

	private final ContentTypeScanner scanner;

	public ContentTypeParser(ContentTypeScanner scanner)
	{
		this.scanner = scanner;
	}

	public ContentType parse() throws IOException,
		ParseException
	{

		Map<String, String> parameters = new LinkedHashMap<>();

		Object current = scanner.scan();
		if (!(current instanceof String type))
			throw new ParseException("expected type and found " + current, 0);

		current = scanner.scan();
		if (!Character.valueOf('/').equals(current))
			throw new ParseException("expected / and found " + current, 0);

		current = scanner.scan();
		if (!(current instanceof String subtype))
			throw new ParseException("expected subtype and found " + current, 0);

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

		return new ContentType(type, subtype, parameters);
	}

	@Override
	public void close()
	{
		scanner.close();
	}
}
