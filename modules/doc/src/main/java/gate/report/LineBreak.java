package gate.report;

public class LineBreak extends ReportElement
{

	private final Report report;

	public LineBreak()
	{
		this(null, new Style());
	}

	public LineBreak(Report report)
	{
		this(report, new Style());
	}

	public LineBreak(Style style)
	{
		this(null, style);
	}

	public LineBreak(Report report, Style style)
	{
		super(style);
		this.report = report;
	}

	@Override
	public LineBreak style(Style style)
	{
		return (LineBreak) super.style(style);
	}

	@Override
	public boolean isEmpty()
	{
		boolean empty = false;
		if (report != null)
			for (var element : report.getElements())
				if (element == this)
					return empty;
				else if (element instanceof LineBreak)
					empty = true;
				else
					empty = false;
		return false;
	}

}
