package gate.report;

/**
 * A generic {@link gate.report.Report} element.
 */
public abstract class ReportElement extends Element
{

	public ReportElement(Style style)
	{
		super(style);
	}

	@Override
	public ReportElement style(Style style)
	{
		return (ReportElement) super.style(style);
	}
}
