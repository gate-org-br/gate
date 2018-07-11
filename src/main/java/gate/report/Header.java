package gate.report;

import java.util.Objects;

public final class Header extends ReportElement
{

	private final Object value;

	Header(Report report, Object value)
	{
		super(report);

		Objects.requireNonNull(value);

		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}
}
