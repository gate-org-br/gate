package gate.type.mime;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.MimeTextFileConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.handler.MimeTextFileHandler;
import gate.lang.contentType.ContentType;
import gate.lang.dataurl.DataURL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Handler(MimeTextFileHandler.class)
@Converter(MimeTextFileConverter.class)
public class MimeTextFile extends MimeText implements MimeFile
{

	private static final long serialVersionUID = 1L;

	private final String name;

	private MimeTextFile(ContentType contentType,
			String charset,
			String text,
			String name)
	{
		super(contentType, charset, text);

		Objects.requireNonNull(name, "Mime name cannot be null");
		this.name = name;
	}

	public static MimeTextFile of(ContentType contentType,
			String charset,
			String text,
			String name)
	{
		return new MimeTextFile(contentType, charset, text, name);
	}

	public static MimeTextFile of(ContentType contentType,
			String charset,
			byte[] data,
			String name)
	{
		return of(contentType, charset, new String(data,
				Charset.forName(charset)), name);

	}

	public static MimeTextFile of(byte[] data, String name)
	{
		return of(ContentType.of("application", "octet-stream"), "UTF-8", data, name);
	}

	public static MimeTextFile of(String text, String name)
	{
		return of(ContentType.of("application", "octet-stream"), "UTF-8", text, name);
	}

	@Override
	public String getName()
	{
		return name;
	}

	public static MimeTextFile of(File file)
	{
		try (BufferedReader reader
				= new BufferedReader(new FileReader(file)))
		{
			try (StringWriter string = new StringWriter())
			{
				for (int c = reader.read(); c != -1; c = reader.read())
					string.write(c);
				string.flush();
				return MimeTextFile.of(file.getName(), string.toString());
			}
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	public String toString()
	{
		try
		{
			Map<String, String> map = new HashMap<>();
			map.put("filename", name);
			map.put("charset", getCharset());

			return DataURL.of(getContentType(), false, map,
					URLEncoder.encode(getText(), getCharset())).toString();
		} catch (UnsupportedEncodingException ex)
		{
			throw new AppError(ex);
		}
	}

	public static MimeTextFile parse(String string)
			throws ConversionException
	{
		try
		{
			DataURL dataURL = DataURL.parse(string);

			String charset = dataURL.getParameters().getOrDefault("charset", "utf-8");

			String text = dataURL.isBase64()
					? new String(Base64.getDecoder().decode(dataURL.getData()), charset)
					: URLDecoder.decode(string, charset);

			return MimeTextFile.of(dataURL.getContentType(), charset, text,
					dataURL.getParameters().get("filename"));
		} catch (ParseException | UnsupportedEncodingException ex)
		{
			throw new ConversionException("invalid data url: " + string);
		}
	}
}
