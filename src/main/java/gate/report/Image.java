package gate.report;

import gate.io.ByteArrayReader;
import gate.type.mime.MimeData;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public final class Image extends ReportElement
{

	private final byte[] source;

	public Image(MimeData source)
	{
		super(new Style());
		this.source = source != null ? source.getData() : null;
	}

	public Image(byte[] source)
	{
		super(new Style());
		this.source = source;
	}

	public Image(URL resource)
	{
		super(new Style());
		try
		{
			this.source = ByteArrayReader.read(resource);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public Object getSource()
	{
		return source;
	}

	@Override
	public Image style(Style style)
	{
		return (Image) super.style(style);
	}
}
