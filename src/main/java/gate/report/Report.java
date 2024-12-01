package gate.report;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.annotation.Name;
import gate.converter.EnumStringConverter;
import gate.error.ConversionException;
import gate.handler.ReportHandler;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
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

	public final ReportList addList(ReportList.Type type)
	{
		ReportList list = new ReportList(type);
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

	public static Report fromJson(JsonObject json) throws ConversionException
	{
		Report report = new Report();

		if (json.get("name") instanceof JsonString name)
		{
			report.addHeader(name.toString());
			report.addLineBreak();
		}

		if (json.get("queries") instanceof JsonArray queries)
		{
			queries.stream()
					.filter(e -> e instanceof JsonObject)
					.map(e -> (JsonObject) e)
					.forEach(query ->
					{
						if (query.get("headers") instanceof JsonArray headers
								&& query.get("dataset") instanceof JsonArray dataset)
						{
							var grid = report.addGrid(JsonElement.class, dataset);

							if (query.get("caption") instanceof JsonString caption)
								grid.setCaption(caption.toString());

							for (int i = 0; i < headers.size(); i++)
							{
								var index = i;
								var header = headers.get(i);
								grid.add().head(header).body(e -> ((JsonArray) e).get(index).toString());
							}
						}
					});

		}
		return report;
	}
}
