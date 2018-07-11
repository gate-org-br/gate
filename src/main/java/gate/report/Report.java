package gate.report;

import gate.annotation.Handler;
import gate.handler.ReportHandler;
import gate.type.mime.MimeData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Model from where documents of various types can be generated.
 */
@Handler(ReportHandler.class)
public class Report
{

	private String name;
	private final Orientation orientation;
	private final List<ReportElement> elements = new ArrayList<>();

	/**
	 * Constructs a new Report with the PORTRAIT orientation.
	 */
	public Report()
	{
		this.orientation = Orientation.PORTRAIT;
	}

	/**
	 * Constructs a new Report with the specified orientation.
	 *
	 * @param orientation the orientation of the reported to be constructed
	 */
	public Report(Orientation orientation)
	{
		Objects.requireNonNull(orientation);
		this.orientation = orientation;
	}

	/**
	 * Returns the orientation of this report.
	 *
	 * @return the orientation of this report
	 */
	public Orientation getOrientation()
	{
		return orientation;
	}

	/**
	 * Returns the name of this report.
	 *
	 * @return the name of this report
	 */
	public String getName()
	{
		if (name == null)
			name = "relatorio";
		return name;
	}

	/**
	 * Modifies the name of this report.
	 *
	 * @param name the new name of this report
	 *
	 * @return this, for chained invocations
	 */
	public Report setName(String name)
	{
		this.name = name;
		return this;
	}

	public List<ReportElement> getElements()
	{
		return Collections.unmodifiableList(elements);
	}

	public <T> Grid<T> addGrid(Class<T> type, Iterable<T> data)
	{
		Grid<T> grid = new Grid<>(this, data);
		elements.add(grid);
		return grid;
	}

	public Form addForm(int columns)
	{
		Form form = new Form(this, columns);
		elements.add(form);
		return form;
	}

	public Form addForm(gate.type.Form form)
	{
		Form f = new Form(this, form);
		elements.add(f);
		return f;
	}

	public void addLineBreak()
	{
		elements.add(new LineBreak(this));
	}

	public void addPageBreak()
	{
		elements.add(new PageBreak(this));
	}

	public void addHeader(Object value)
	{
		elements.add(new Header(this, value));
	}

	public void addParagraph(Object value)
	{
		elements.add(new Paragraph(this, value));
	}

	public void addParagraph(Object value, String style)
	{
		elements.add(new Paragraph(this, value, style));
	}

	public void addFooter(Object value)
	{
		elements.add(new Footer(this, value));
	}

	public void addImage(byte[] source)
	{
		elements.add(new Image(this, source));
	}

	public void addImage(MimeData source)
	{
		if (source != null)
			elements.add(new Image(this, source));
	}

	public enum Orientation
	{
		PORTRAIT, LANDSCAPE
	}
}
