package gate.type.mime;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.MimeTextConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.handler.MimeTextHandler;
import gate.lang.dataurl.DataURL;
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

	private final String type;
	private final String subtype;
	private final String charset;
	private final String text;

	public MimeText(String type, String subtype, String charset, String text)
	{
		Objects.requireNonNull(type, "Mime type cannot be null");
		Objects.requireNonNull(subtype, "Mime subtype cannot be null");
		Objects.requireNonNull(charset, "Mime charset cannot be null");
		Objects.requireNonNull(text, "Mime text cannot be null");

		this.type = type;
		this.subtype = subtype;
		this.charset = charset;
		this.text = text;
	}

	public MimeText(String type, String subtype, String charset, byte[] data)
	{
		Objects.requireNonNull(type, "Mime type cannot be null");
		Objects.requireNonNull(subtype, "Mime subtype cannot be null");
		Objects.requireNonNull(charset, "Mime charset cannot be null");
		Objects.requireNonNull(data, "Mime text cannot be null");

		this.type = type;
		this.subtype = subtype;
		this.charset = charset;
		this.text = new String(data, Charset.forName(charset));
	}

	public MimeText(String text)
	{
		this("text", "plain", "UTF-8", text);
	}

	public MimeText(byte[] data)
	{
		this("text", "plain", "UTF-8", data);
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
	public String getType()
	{
		return type;
	}

	@Override
	public String getSubType()
	{
		return subtype;
	}

	@Override
	public String toString()
	{
		try
		{
			Map map = new HashMap<>();
			map.put("charset", charset);

			return new DataURL(getType(), getSubType(), false, map,
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

			return new MimeText(dataURL.getType(), dataURL.getSubtype(), charset, text);
		} catch (ParseException | UnsupportedEncodingException ex)
		{
			throw new ConversionException("invalid data url: " + string);
		}
	}
}
