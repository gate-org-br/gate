package gate.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

@ApplicationScoped
public class PNGHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value.toString();
		response.setContentType("image/png");
		response.setContentLength(string.length());
		try (Writer writer = response.getWriter())
		{
			writer.write(string);
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}