package gate.doc.printer;

import gate.report.Report;

import java.io.IOException;
import java.io.OutputStream;

public interface Printer
{
	void print(OutputStream outputStream, Report value) throws IOException;
}