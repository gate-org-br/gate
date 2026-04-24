package gate.doc;

import gate.annotation.Icon;
import gate.doc.printer.DOCPrinter;
import gate.lang.contentType.ContentType;
import gate.report.Report;

import java.io.OutputStream;

@Icon("gate.doc.Doc$Type:DOC")
public class DOC extends Doc
{

	public DOC(Report report)
	{
		super(report);
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.of("application", "doc");
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.docx", getReport().getName());
	}

	@Override
	public void print(OutputStream outputStream)
	{
		new DOCPrinter().print(outputStream, getReport());
	}
}