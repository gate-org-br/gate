package gate.report;

import gate.type.mime.MimeData;
import java.util.Objects;

public final class Image extends ReportElement
{

	private final byte[] source;

	Image(Report report, MimeData source)
	{
		this(report, source.getData());
	}

	Image(Report report, byte[] source)
	{
		super(report, new Style());

		Objects.requireNonNull(source);

		this.source = source;
	}

	public Object getSource()
	{
		return source;
	}
}
