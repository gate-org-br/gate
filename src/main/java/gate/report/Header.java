package gate.report;

import java.util.Objects;

public final class Header extends ReportElement
{

	private final Object value;

	public Header(Object value)
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
	public Header style(Style style)
	{
		return (Header) super.style(style);
	}
}
