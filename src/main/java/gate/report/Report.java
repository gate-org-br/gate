package gate.report;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.annotation.Name;
import gate.converter.EnumStringConverter;
import gate.handler.ReportHandler;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
	public final Orientation getOrientation()
	{
		return orientation;
	}

	/**
	 * Returns the name of this report.
	 *
	 * @return the name of this report
	 */
	public final String getName()
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
	public final Report setName(String name)
	{
		this.name = name;
		return this;
	}

	public final List<ReportElement> getElements()
	{
		return elements;
	}

	public final Report add(ReportElement element)
	{
		elements.add(element);
		return this;
	}

	public final <T> Grid<T> addGrid(Class<T> type, Iterable<T> data)
	{
		Grid<T> grid = new Grid<>(data);
		elements.add(grid);
		return grid;
	}

	public final Dictionary addDictionary()
	{
		Dictionary dictionary = new Dictionary();
		elements.add(dictionary);
		return dictionary;
	}

	public final ReportList addList()
	{
		ReportList list = new ReportList();
		elements.add(list);
		return list;
	}

	public final Form addForm(int columns)
	{
		Form form = new Form(columns);
		elements.add(form);
		return form;
	}

	public final Form addForm(gate.type.Form form)
	{
		Form f = new Form(form);
		elements.add(f);
		return f;
	}

	public final LineBreak addLineBreak()
	{
		LineBreak lineBreak = new LineBreak(this);
		elements.add(lineBreak);
		return lineBreak;
	}

	public final PageBreak addPageBreak()
	{
		PageBreak pageBreak = new PageBreak();
		elements.add(pageBreak);
		return pageBreak;
	}

	public final Header addHeader(Object value)
	{
		Header header = new Header(value);
		elements.add(header);
		return header;
	}

	public final Paragraph addParagraph(Object value)
	{
		Paragraph paragraph = new Paragraph(value);
		elements.add(paragraph);
		return paragraph;
	}

	public final Footer addFooter(Object value)
	{
		Footer footer = new Footer(value);
		elements.add(footer);
		return footer;
	}

	public final Image addImage(byte[] source)
	{
		if (source == null)
			return Image.EMPTY;
		Image image = Image.of(source);
		elements.add(image);
		return image;
	}

	public final Image addImage(MimeData source)
	{
		if (source == null)
			return Image.EMPTY;
		Image image = Image.of(source);
		elements.add(image);
		return image;
	}

	public final Image addImage(MimeDataFile source)
	{
		if (source == null)
			return Image.EMPTY;
		Image image = Image.of(source);
		elements.add(image);
		return image;
	}

	public final Image addImage(URL source)
	{
		if (source == null)
			return Image.EMPTY;
		Image image = Image.of(source);
		elements.add(image);
		return image;
	}

	public final Image addImage(File source)
	{
		if (source == null)
			return Image.EMPTY;

		Image image = Image.of(source);
		elements.add(image);
		return image;
	}

	public final <T> Chart<T> addChart(Class<T> type, Collection<T> dataset,
			Chart.Format format)
	{
		Chart chart = new Chart(type, dataset, format);
		elements.add(chart);
		return chart;
	}

	/**
	 * Remove empty information from the report.
	 *
	 * @return the same object, for chained invocations
	 */
	public final Report compact()
	{
		getElements().forEach(Element::compact);
		getElements().removeIf(Element::isEmpty);
		return this;
	}

	@Converter(EnumStringConverter.class)
	public enum Orientation
	{
		@Name("Retrato")
		PORTRAIT,
		@Name("Paisagem")
		LANDSCAPE
	}

	public static Report of(JsonObject jsonObject) throws IllegalArgumentException
	{
		var orientation = jsonObject.getString("orientation")
				.map(String::toUpperCase)
				.map(e -> switch (e)
		{
			case "PORTRAIT" ->
				Report.Orientation.PORTRAIT;
			case "LANDSCAPE" ->
				Report.Orientation.LANDSCAPE;
			default ->
				throw new IllegalArgumentException("Invalid report orientation");
		}).orElse(Report.Orientation.PORTRAIT);

		Report report = new Report(orientation);

		if (jsonObject.get("type") instanceof JsonString)
			report.add(ReportElement.of(jsonObject));
		else if (jsonObject.get("elements") instanceof JsonArray elements)
			elements.stream()
					.map(ReportElement::of)
					.filter(Objects::nonNull)
					.forEach(report::add);
		else if (jsonObject.get("columns") instanceof JsonArray
				&& jsonObject.get("dataset") instanceof JsonArray)
			report.add(Grid.of(jsonObject));
		else
			report.add(Dictionary.of(jsonObject));
		return report;
	}

	public static Report of(JsonArray jsonArray) throws IllegalArgumentException
	{
		Report report = new Report();
		if (jsonArray.stream().allMatch(e -> e instanceof JsonString))
			report.add(ReportList.of(jsonArray));
		else
			report.add(Grid.of(jsonArray));
		return report;
	}

	public static Report of(JsonString jsonString) throws IllegalArgumentException
	{
		Report report = new Report();
		report.add(new Paragraph(jsonString.toString()));
		return report;
	}
}
