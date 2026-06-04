package gate.doc.printer;

import gate.adapter.renderer.Renderer;

import gate.error.ConversionException;
import gate.lang.csv.CSVFormatter;
import gate.report.Column;
import gate.report.Grid;
import gate.report.Report;
import gate.report.ReportElement;
import gate.util.Toolkit;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates CSV documents from objects of type {@link gate.report.Report}.
 */
public class CSVPrinter implements Printer
{

	private PrintWriter writer;

	@Override
	public void print(OutputStream os, Report report)
	{
		try (PrintWriter writer = new PrintWriter(os, true, StandardCharsets.UTF_8))
		{
			this.writer = writer;

			for (ReportElement element : report.getElements())
				if (element instanceof Grid grid)
					printGrid(grid, grid.getData());
		} catch (ConversionException e)
		{
			throw new RuntimeException(e);
		} finally
		{
			this.writer = null;
		}
	}

	private void writeLine(List<String> values)
	{
		CSVFormatter formatter = CSVFormatter.of(writer);
		formatter.writeLine(values);
	}

	private void printGrid(Grid<Object> grid, Object data) throws ConversionException
	{
		if (grid.getColumns().stream().anyMatch(e -> e.getHead() != null))
			writeLine(grid.getColumns().stream().map(Column::getHead).map(Renderer::render)
					.collect(Collectors.toList()));

		for (Object obj : Toolkit.iterable(data))
			if (obj != null)
			{

				writeLine(grid.getColumns().stream().map(e -> Renderer.render(e.getBody().apply(obj)))
						.collect(Collectors.toList()));

				if (grid.getChildren() != null)
					for (Object child : Toolkit.collection(grid.getChildren().apply(obj)))
						printGrid(grid, child);
			}

		if (grid.getColumns().stream().anyMatch(e -> e.getFoot() != null))
			writeLine(grid.getColumns().stream().map(Column::getFoot).map(Renderer::render)
					.collect(Collectors.toList()));
	}
}