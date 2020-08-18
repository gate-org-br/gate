package gate.type.mime;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.MimeDataConverter;
import gate.error.ConversionException;
import gate.handler.MimeDataHandler;
import gate.io.Processor;
import gate.io.Reader;
import gate.lang.dataurl.DataURL;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;

@Handler(MimeDataHandler.class)
@Converter(MimeDataConverter.class)
public class MimeData implements Mime
{

	private static final long serialVersionUID = 1L;

	private final String type;
	private final String subtype;
	private final byte[] data;

	public MimeData(String type, String subtype, byte[] data)
	{
		Objects.requireNonNull(type, "Mime type cannot be null");
		Objects.requireNonNull(subtype, "Mime subtype cannot be null");
		Objects.requireNonNull(data, "Mime data cannot be null");
		this.type = type;
		this.subtype = subtype;
		this.data = data;
	}

	public MimeData(byte[] data)
	{
		this("application",
			"octet-stream", data);
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

	public byte[] getData()
	{
		return data;
	}

	public int getSize()
	{
		return data.length;
	}

	@Override
	public String toString()
	{
		return new DataURL(type, subtype, true, Collections.emptyMap(),
			Base64.getEncoder().encodeToString(data)).toString();
	}

	public static MimeData parse(String string) throws ConversionException
	{
		try
		{
			DataURL dataURL = DataURL.parse(string);
			if (!dataURL.isBase64())
				throw new ConversionException("a binary data url must be on base 64 format");
			return new MimeData(dataURL.getType(),
				dataURL.getSubtype(),
				Base64.getDecoder().decode(dataURL.getData()));

		} catch (ParseException ex)
		{
			throw new ConversionException("invalid data url: " + string);
		}
	}

	public <T> T load(Reader<T> loader) throws IOException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(getData()))
		{
			return loader.read(stream);
		}
	}

	public <T> void process(Processor<T> processor) throws IOException, InvocationTargetException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(getData()))
		{
			processor.process(stream);
		}
	}
}
