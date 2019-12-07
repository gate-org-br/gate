package gate.report;

public class PageBreak extends ReportElement
{

	public PageBreak()
	{
		super(new Style());
	}

	@Override
	public PageBreak style(Style style)
	{
		return (PageBreak) super.style(style);
	}
}
