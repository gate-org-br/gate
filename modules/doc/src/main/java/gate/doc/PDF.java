package gate.doc;

import gate.annotation.Icon;
import gate.doc.printer.PDFPrinter;
import gate.lang.contentType.ContentType;
import gate.report.Report;

import java.io.OutputStream;

@Icon("gate.doc.Doc$Type:PDF")
public class PDF extends Doc
{

	public PDF(Report report)
	{
		super(report);
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.of("application", "pdf");
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.pdf", getReport().getName());
	}

	@Override
	public void print(OutputStream outputStream)
	{
		new PDFPrinter().print(outputStream, getReport());
	}
}