package gate.adapter.handler;

import gate.doc.Document;
import gate.error.AppError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Locale;

@ApplicationScoped
public class DocHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		Document doc = (Document) value;
		response.setLocale(Locale.getDefault());
		response.setContentType(doc.getContentType().toString());
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", doc.getFileName()));
		try (OutputStream os = response.getOutputStream())
		{
			((Document) value).print(os);
			os.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}