package gate.lang.contentDisposition;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public record ContentDisposition(String value, Map<String, String> parameters)
{

	public ContentDisposition
	{
		Objects.requireNonNull(parameters);
	}


	@Override
	public Map<String, String> parameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();

		if (value != null)
			string.append(value);

		parameters.forEach((key, value1) ->
				string.append(';').append(key).append('=').append(URLEncoder.encode(value1, StandardCharsets.UTF_8)));

		return string.toString();
	}

	public static ContentDisposition valueOf(String string)
	{
		try (ContentDispositionParser parser = new ContentDispositionParser(
				new ContentDispositionScanner(new StringReader(string))))
		{
			try
			{
				return parser.parse();
			} catch (ParseException ex)
			{
				throw new IllegalArgumentException(string + " is not a valid disposition type");
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}
	}
}
