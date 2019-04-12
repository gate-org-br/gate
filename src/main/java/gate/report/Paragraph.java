package gate.report;

import java.util.Objects;

public final class Paragraph extends ReportElement
{

	private final Object value;

	Paragraph(Report report, Object value)
	{
		super(report, new Style());

		Objects.requireNonNull(value);

		this.value = value;
	}

	Paragraph(Report report, Object value, String style)
	{
		super(report, new Style());

		Objects.requireNonNull(value);

		this.value = value;

	}

	public Object getValue()
	{
		return value;
	}
}
