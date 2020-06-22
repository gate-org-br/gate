package gate.report;

import java.util.Objects;

public final class Paragraph extends ReportElement
{

	private final Object value;

	public Paragraph(Object value)
	{
		super(new Style().justify());

		Objects.requireNonNull(value);

		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public Paragraph style(Style style)
	{
		return (Paragraph) super.style(style);
	}
}
