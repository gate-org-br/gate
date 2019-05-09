package gate.report;

import gate.annotation.Handler;
import gate.handler.ReportHandler;
import gate.type.mime.MimeData;
import java.net.URL;
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

	public Report add(ReportElement element)
	{
		elements.add(element);
		return this;
	}

	public <T> Grid<T> addGrid(Class<T> type, Iterable<T> data)
	{
		Grid<T> grid = new Grid<>(data);
		elements.add(grid);
		return grid;
	}

	public Form addForm(int columns)
	{
		Form form = new Form(columns);
		elements.add(form);
		return form;
	}

	public Form addForm(gate.type.Form form)
	{
		Form f = new Form(form);
		elements.add(f);
		return f;
	}

	public LineBreak addLineBreak()
	{
		LineBreak lineBreak = new LineBreak();
		elements.add(lineBreak);
		return lineBreak;
	}

	public PageBreak addPageBreak()
	{
		PageBreak pageBreak = new PageBreak();
		elements.add(pageBreak);
		return pageBreak;
	}

	public Header addHeader(Object value)
	{
		Header header = new Header(value);
		elements.add(header);
		return header;
	}

	public Paragraph addParagraph(Object value)
	{
		Paragraph paragraph = new Paragraph(value);
		elements.add(new Paragraph(value));
		return paragraph;
	}

	public Paragraph addParagraph(Object value, String style)
	{
		Paragraph paragraph = new Paragraph(this, value, style);
		elements.add(paragraph);
		return paragraph;
	}

	public Footer addFooter(Object value)
	{
		Footer footer = new Footer(value);
		elements.add(footer);
		return footer;
	}

	public Image addImage(byte[] source)
	{
		Image image = new Image(source);
		elements.add(new Image(source));
		return image;
	}

	public Image addImage(MimeData source)
	{
		Image image = new Image(source);
		elements.add(new Image(source));
		return image;
	}

	public Image addImage(URL source)
	{
		Image image = new Image(source);
		elements.add(new Image(source));
		return image;
	}

	public enum Orientation
	{
		PORTRAIT, LANDSCAPE
	}
}
