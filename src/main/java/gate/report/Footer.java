package gate.report;

import java.util.Objects;

public final class Footer extends ReportElement
{

	private final Object value;

	Footer(Report report, Object value)
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
