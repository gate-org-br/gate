package gate.lang.contentDisposition;

import gate.error.AppError;
import gate.error.ConversionException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ContentDisposition
{

	private final String value;
	private final Map<String, String> parameters;

	public ContentDisposition(String value,
		Map<String, String> parameters)
	{
		Objects.requireNonNull(parameters);
		this.value = value;
		this.parameters = parameters;
	}

	public String getValue()
	{
		return value;
	}

	public Map<String, String> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();

		if (value != null)
			string.append(value);

		parameters
			.entrySet().forEach(e ->
			{
				try
				{
					string.append(';').append(e.getKey()).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException ex)
				{
					throw new UncheckedIOException(ex);
				}
			});

		return string.toString();
	}

	public static ContentDisposition parse(String string) throws ParseException
	{
		try ( ContentDispositionParser parser = new ContentDispositionParser(new ContentDispositionScanner(new StringReader(string))))
		{
			try
			{
				return parser.parse();
			} catch (IOException ex)
			{
				throw new AppError(ex);
			}
		}
	}

	public static ContentDisposition valueOf(String string) throws ConversionException
	{
		try
		{
			return parse(string);
		} catch (ParseException ex)
		{
			throw new ConversionException("Invalid content disposition header");
		}
	}
}
