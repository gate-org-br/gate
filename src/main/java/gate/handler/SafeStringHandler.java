package gate.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class SafeStringHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		response.setContentType("text/plain");

		try (Writer writer = response.getWriter())
		{
			writer.write(value.toString());
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
