package gate.doc;

import gate.annotation.Icon;
import gate.doc.printer.XLSPrinter;
import gate.lang.contentType.ContentType;
import gate.report.Report;

import java.io.OutputStream;

@Icon("gate.doc.Doc$Type:XLS")
public class XLS extends Doc
{

	public XLS(Report report)
	{
		super(report);
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.of("application", "vnd.ms-excel");
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.xlsx", getReport().getName());
	}

	@Override
	public void print(OutputStream outputStream) {new XLSPrinter().print(outputStream, getReport());}
}