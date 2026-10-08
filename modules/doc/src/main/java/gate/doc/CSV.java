package gate.doc;

import gate.annotation.Icon;
import gate.doc.printer.CSVPrinter;
import gate.lang.contentType.ContentType;
import gate.report.Report;

import java.io.OutputStream;

@Icon("gate.doc.Document$Type:CSV")
public class CSV extends Document
{

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
	public void print(OutputStream outputStream)
	{
		new CSVPrinter().print(outputStream, getReport());
	}
}