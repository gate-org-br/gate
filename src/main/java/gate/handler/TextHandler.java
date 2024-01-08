package gate.handler;

import gate.Progress;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class TextHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value.toString();
		response.setContentType("text/plain");

		try (Writer writer = response.getWriter())
		{
			writer.write(string);
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		Progress progress, Object value)
	{
		progress.result("text/plain", null, value.toString());
	}
}
