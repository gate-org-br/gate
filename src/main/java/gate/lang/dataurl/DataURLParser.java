package gate.lang.dataurl;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataURLParser implements AutoCloseable
{

	private final DataURLScanner scanner;

	public DataURLParser(DataURLScanner scanner)
	{
		this.scanner = scanner;
	}

	public DataURL parse() throws IOException,
		ParseException
	{
		Object current = scanner.scan();
		if (!"data".equals(current))
			throw new ParseException("expected data and found " + current, 0);

		current = scanner.scan();
		if (!Character.valueOf(':').equals(current))
			throw new ParseException("expected : and found " + current, 0);

		String type = null;
		String subtype = null;
		boolean base64 = false;
		Map<String, String> parameters = new LinkedHashMap<>();

		current = scanner.scan();
		if (!Character.valueOf(',').equals(current))
		{
			if (!(current instanceof String))
				throw new ParseException("expected type and found " + current, 0);
			type = (String) current;
			current = scanner.scan();

			if (Character.valueOf('/').equals(current))
			{
				current = scanner.scan();
				if (!(current instanceof String))
					throw new ParseException("expected subtype and found " + current, 0);
				subtype = (String) current;
				current = scanner.scan();
			}

			while (Character.valueOf(';').equals(current))
			{
				current = scanner.scan();
				if ("base64".equals(current))
				{
					base64 = true;
					current = scanner.scan();
					break;
				}

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
			}
		}

		if (!Character.valueOf(',').equals(current))
			throw new ParseException("expected , and found " + current, 0);
		return new DataURL(type, subtype, base64, parameters, scanner.finish());
	}

	@Override
	public void close()
	{
		scanner.close();
	}
}
