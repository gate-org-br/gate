package gate.type.mime;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.MimeTextConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.handler.MimeTextHandler;
import gate.lang.contentType.ContentType;
import gate.lang.dataurl.DataURL;
import static gate.sql.update.Update.type;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Handler(MimeTextHandler.class)
@Converter(MimeTextConverter.class)
public class MimeText implements Mime
{

	private static final long serialVersionUID = 1L;

	private final String charset;
	private final String text;
	private final ContentType contentType;

	protected MimeText(ContentType contentType, String charset, String text)
	{
		Objects.requireNonNull(contentType, "Mime type cannot be null");
		Objects.requireNonNull(charset, "Mime charset cannot be null");
		Objects.requireNonNull(text, "Mime text cannot be null");

		this.contentType = contentType;
		this.charset = charset;
		this.text = text;
	}

	private static MimeText of(ContentType contentType, String charset, String text)
	{
		return new MimeText(contentType, charset, text);
	}

	public static MimeText of(ContentType contentType, String charset, byte[] data)
	{
		Objects.requireNonNull(data, "Mime data cannot be null");
		return of(contentType, charset, new String(data, Charset.forName(charset)));
	}

	public static MimeText of(String text)
	{
		return of(ContentType.of("text", "plain"), "UTF-8", text);
	}

	public static MimeText of(byte[] data)
	{
		return of(ContentType.of("text", "plain"), "UTF-8", data);
	}

	public String getCharset()
	{
		return charset;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public ContentType getContentType()
	{
		return contentType;
	}

	@Override
	public String toString()
	{
		try
		{
			Map map = new HashMap<>();
			map.put("charset", charset);

			return DataURL.of(getContentType(), false, map,
					URLEncoder.encode(getText(), charset)).toString();
		} catch (UnsupportedEncodingException ex)
		{
			throw new AppError(ex);
		}
	}

	public static MimeText parse(String string)
			throws ConversionException
	{
		try
		{
			DataURL dataURL = DataURL.parse(string);

			String charset = dataURL.getParameters().getOrDefault("charset", "utf-8");

			String text = dataURL.isBase64()
					? new String(Base64.getDecoder().decode(dataURL.getData()), charset)
					: URLDecoder.decode(string, charset);

			return new MimeText(dataURL.getContentType(), charset, text);
		} catch (ParseException | UnsupportedEncodingException ex)
		{
			throw new ConversionException("invalid data url: " + string);
		}
	}
}
