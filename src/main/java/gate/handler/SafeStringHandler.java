package gate.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
