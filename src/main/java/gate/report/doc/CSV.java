package gate.report.doc;

import gate.annotation.Icon;
import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.csv.CSVFormatter;
import gate.report.Column;
import gate.report.Grid;
import gate.report.Report;
import gate.report.ReportElement;
import gate.util.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;

/**
 * Generates CSV documents from objects of type {@link gate.report.Report}.
 */
@Icon("2222")
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
	public String getContentType()
	{
		return "text";
	}

	@Override
	public String getContentSubtype()
	{
		return "csv";
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.csv", getReport().getName());
	}

	@Override
	public void print(OutputStream os)
	{
		try (PrintWriter writer = new PrintWriter(os))
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
		CSVFormatter formatter = new CSVFormatter(writer);
		for (Object obj : Toolkit.iterable(data))
		{
			if (obj != null)
			{

				try
				{
					formatter.writeLine(grid.getColumns().stream()
						.map(e -> Converter.toString(e.getBody().apply(obj)))
						.collect(Collectors.toList()));
				} catch (IOException ex)
				{
					throw new ConversionException(ex.getMessage(), ex);
				}

				if (grid.getChildren() != null)
					for (Object child : Toolkit.collection(grid.getChildren().apply(obj)))
						print(writer, grid, child);
			}
		}
	}
}
