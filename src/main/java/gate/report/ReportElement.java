package gate.report;

import java.util.Objects;

/**
 * A generic {@link gate.report.Report} element.
 */
public abstract class ReportElement extends Element
{

	private final Report report;

	ReportElement(Report report)
	{
		Objects.requireNonNull(report);

		this.report = report;
	}

	/**
	 * Return the report associated with this element.
	 *
	 * @return the report associated with this element
	 */
	public Report getReport()
	{
		return report;
	}
}
