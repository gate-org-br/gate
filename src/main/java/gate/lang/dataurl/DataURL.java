package gate.lang.dataurl;

import gate.error.AppError;
import gate.lang.contentType.ContentType;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class DataURL
{

	private final ContentType contentType;
	private final boolean base64;
	private final Map<String, String> parameters;
	private final String data;

	private DataURL(ContentType contentType,
			boolean base64,
			Map<String, String> parameters,
			String data)
	{
		Objects.requireNonNull(parameters);
		this.contentType = contentType;
		this.base64 = base64;
		this.parameters = parameters;
		this.data = data;
	}

	public static DataURL of(ContentType contentType,
			boolean base64,
			Map<String, String> parameters,
			String data)
	{
		return new DataURL(contentType, base64, parameters, data);
	}

	public ContentType getContentType()
	{
		return contentType;
	}

	public String getData()
	{
		return data;
	}

	public Map<String, String> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public boolean isBase64()
	{
		return base64;
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();

		string.append("data:");

		if (contentType != null)
			string.append(contentType);

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

		if (base64)
			string.append(";base64");

		string.append(',')
				.append(data);
		return string.toString();
	}

	public static DataURL parse(String string) throws ParseException
	{
		try (DataURLParser parser = new DataURLParser(new DataURLScanner(new StringReader(string))))
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

	public long getDecodedDataSize()
	{
		if (base64)
		{
			long length = (long) Math.ceil(((double) data.length() / 3));
			if (data.endsWith("=="))
				return length - 2;
			else if (data.endsWith("="))
				return length - 1;
			return length;
		}

		return data.length();
	}
}
