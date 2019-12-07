package gate.report;

import java.util.Objects;

public final class Footer extends ReportElement
{

	private final Object value;

	public Footer(Object value)
	{
		super(new Style());

		Objects.requireNonNull(value);

		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public Footer style(Style style)
	{
		return (Footer) super.style(style);
	}
}
