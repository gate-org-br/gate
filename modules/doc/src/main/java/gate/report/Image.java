package gate.report;

import gate.lang.dataurl.DataURL;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import gate.util.Resource;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.Base64;

public final class Image extends ReportElement
{

	private final byte[] source;
	private final String filename;

	public static final Image EMPTY = new Image("empty", new byte[0]);

	private Image(String filename, byte[] source)
	{
		super(new Style());
		this.source = source;
		this.filename = filename;
	}

	public static Image of(MimeData source)
	{
		return new Image("image.png", source.getData());
	}

	public static Image of(byte[] source)
	{
		return new Image("image.png", source);
	}

	public static Image of(MimeDataFile source)
	{
		return new Image(source.getName(), source.getData());
	}

	public static Image of(String source)
	{
		try
		{
			var dataUrl = DataURL.valueOf(source);
			return new Image(dataUrl.getParameters().getOrDefault("filename",
					"%s.%s".formatted(dataUrl.getContentType().getType(),
							dataUrl.getContentType().getSubtype())),
					Base64.getDecoder().decode(dataUrl.getData()));
		} catch (ParseException ex)
		{
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public static Image of(JsonObject jsonObject)
	{
		if (jsonObject.get("data") instanceof JsonString data)
		{
			var image = of(data.unwrap());

			if (jsonObject.get("style") instanceof JsonString style)
				image.style(Style.of(style));
			else if (jsonObject.get("style") instanceof JsonObject style)
				image.style(Style.of(style));

			return image;
		}

		throw new IllegalArgumentException("Attempt to create image wihout specifying the data property");
	}

	public static Image of(URL resource)
	{
		return new Image(new File(resource.getPath()).getName(),
				Resource.readBytes(resource));
	}

	public static Image of(File file)
	{
		try
		{
			return new Image(file.getName(), java.nio.file.Files.readAllBytes(file.toPath()));
		} catch (java.io.IOException ex)
		{
			throw new java.io.UncheckedIOException(ex);
		}
	}

	public byte[] getSource()
	{
		return source;
	}

	public String getFilename()
	{
		return filename;
	}

	@Override
	public Image style(Style style)
	{
		return (Image) super.style(style);
	}
}
