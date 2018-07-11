package gate.handler;

import gate.error.AppError;
import gate.report.doc.Doc;
import java.io.OutputStream;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		Doc doc = (Doc) value;
		response.setLocale(Locale.getDefault());
		response.setContentType(doc.getContentType());
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", doc.getFileName()));
		try (OutputStream os = response.getOutputStream())
		{
			((Doc) value).print(os);
			os.flush();
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
