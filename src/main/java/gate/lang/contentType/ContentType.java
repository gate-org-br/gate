package gate.lang.contentType;

import gate.error.AppError;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ContentType
{

	private final String type;
	private final String subtype;
	private final Map<String, String> parameters;

	private ContentType(String type,
			String subtype,
			Map<String, String> parameters)
	{
		Objects.requireNonNull(parameters);
		this.type = type;
		this.subtype = subtype;
		this.parameters = parameters;
	}

	public String getType()
	{
		return type;
	}

	public String getSubtype()
	{
		return subtype;
	}

	public Map<String, String> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public static ContentType of(String type, String subtype,
			Map<String, String> parameters)
	{
		return new ContentType(type, subtype, parameters);
	}

	public static ContentType of(String type, String subtype)
	{
		return new ContentType(type, subtype, Map.of());
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();

		if (type != null)
			string.append(type);
		if (subtype != null)
			string.append('/').append(subtype);

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

	public static ContentType valueOf(String string)
	{
		try (ContentTypeParser parser = new ContentTypeParser(new ContentTypeScanner(new StringReader(string))))
		{
			try
			{
				return parser.parse();
			} catch (ParseException ex)
			{
				throw new IllegalArgumentException(string + " is not a valid content type");
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}
	}
}
