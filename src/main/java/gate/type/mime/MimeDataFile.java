package gate.type.mime;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.MimeDataFileConverter;
import gate.error.ConversionException;
import gate.handler.MimeDataFileHandler;
import gate.lang.dataurl.DataURL;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Handler(MimeDataFileHandler.class)
@Converter(MimeDataFileConverter.class)
public class MimeDataFile extends MimeData implements MimeFile
{

	private static final long serialVersionUID = 1L;

	private final String name;

	public MimeDataFile(String type,
		String subtype,
		byte[] data,
		String name)
	{
		super(type, subtype, data);

		Objects.requireNonNull(name, "Mime file name cannot be null.");

		this.name = name;
	}

	public MimeDataFile(byte[] data, String name)
	{
		this("application", "octet-stream", data, name);
	}

	@Override
	public String getName()
	{
		return name;
	}

	public static MimeDataFile of(File file)
	{
		return of(file, file.getName());
	}

	public static MimeDataFile of(File file, String name)
	{
		try (BufferedInputStream stream
			= new BufferedInputStream(new FileInputStream(file)))
		{
			try (ByteArrayOutputStream bytes = new ByteArrayOutputStream())
			{
				for (int c = stream.read(); c != -1; c = stream.read())
					bytes.write(c);
				bytes.flush();
				return new MimeDataFile(bytes.toByteArray(), name);
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public static MimeDataFile of(URL url)
	{
		return of(url, url.getFile());
	}

	public static MimeDataFile of(URL url, String name)
	{
		try (BufferedInputStream stream
			= new BufferedInputStream(url.openStream()))
		{
			try (ByteArrayOutputStream bytes = new ByteArrayOutputStream())
			{
				for (int c = stream.read(); c != -1; c = stream.read())
					bytes.write(c);
				bytes.flush();
				return new MimeDataFile(bytes.toByteArray(), name);
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public String toString()
	{
		Map<String, String> map = new HashMap<>();
		map.put("filename", name);
		return new DataURL(getType(), getSubType(), true, map,
			Base64.getEncoder().encodeToString(getData())).toString();
	}

	public static MimeDataFile parse(String string)
		throws ConversionException
	{
		try
		{
			DataURL dataURL = DataURL.parse(string);
			if (!dataURL.isBase64())
				throw new ConversionException("a binary data url must be on base 64 format");
			return new MimeDataFile(dataURL.getType(),
				dataURL.getSubtype(),
				Base64.getDecoder().decode(dataURL.getData()),
				dataURL.getParameters().get("filename"));
		} catch (ParseException ex)
		{
			throw new ConversionException("invalid data url: " + string);
		}
	}
}
