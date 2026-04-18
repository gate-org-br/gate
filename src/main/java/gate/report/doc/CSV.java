package gate.report.doc;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import gate.annotation.Icon;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.contentType.ContentType;
import gate.lang.csv.CSVFormatter;
import gate.report.Column;
import gate.report.Doc;
import gate.report.Grid;
import gate.report.Report;
import gate.report.ReportElement;
import gate.util.Toolkit;

/**
 * Generates CSV documents from objects of type {@link gate.report.Report}.
 */
@Icon("gate.report.Doc$Type:CSV")
public class CSV extends Doc
{

	/**
	 * Constructs a new CSV Doc for the specified report.
	 *
	 * @param report the report to be used to generate the document
	 */
	public CSV(Report report)
	{
		super(report);
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.of("text", "csv");
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.csv", getReport().getName());
	}

	@Override
	public void print(OutputStream os)
	{
		try (PrintWriter writer = new PrintWriter(os, true, Charset.forName("UTF-8")))
		{
			for (ReportElement element : getReport().getElements())
				if (element instanceof Grid)
					print(writer, (Grid) element, ((Grid) element).getData());
		} catch (ConversionException e)
		{
			throw new AppError(e);
		}
	}

	private void print(PrintWriter writer, Grid<Object> grid, Object data) throws ConversionException
	{
		CSVFormatter formatter = CSVFormatter.of(writer);

		if (grid.getColumns().stream().anyMatch(e -> e.getHead() != null))
			formatter.writeLine(grid.getColumns().stream().map(Column::getHead).map(Converter::render)
					.collect(Collectors.toList()));

		for (Object obj : Toolkit.iterable(data))
			if (obj != null)
			{

				formatter.writeLine(grid.getColumns().stream().map(e -> Converter.render(e.getBody().apply(obj)))
						.collect(Collectors.toList()));

				if (grid.getChildren() != null)
					for (Object child : Toolkit.collection(grid.getChildren().apply(obj)))
						print(writer, grid, child);
			}

		if (grid.getColumns().stream().anyMatch(e -> e.getFoot() != null))
			formatter.writeLine(grid.getColumns().stream().map(Column::getFoot).map(Converter::render)
					.collect(Collectors.toList()));
	}
}
