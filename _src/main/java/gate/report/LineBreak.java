package gate.report;

public class LineBreak extends ReportElement
{

	public LineBreak()
	{
		super(new Style());
	}

	@Override
	public LineBreak style(Style style)
	{
		return (LineBreak) super.style(style);
	}

}
