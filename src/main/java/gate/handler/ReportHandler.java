package gate.handler;

import gate.report.Report;
import gate.report.doc.PDF;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ReportHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		response.setLocale(Locale.getDefault());
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition",
			String.format("attachment; filename=\"%s.pdf\"",
				((Report) value).getName()));

		try ( OutputStream os = response.getOutputStream())
		{
			new PDF((Report) value).print(os);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
