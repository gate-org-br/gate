package gate.report;

import gate.io.ByteArrayReader;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

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

	public static Image of(URL resource)
	{
		try
		{
			return new Image(new File(resource.getPath()).getName(),
				ByteArrayReader.read(resource));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public static Image of(File file)
	{
		try
		{
			return new Image(file.getName(), ByteArrayReader.read(file));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
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
