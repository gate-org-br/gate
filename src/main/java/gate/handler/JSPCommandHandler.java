package gate.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;

@ApplicationScoped
public class JSPCommandHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			request.getRequestDispatcher(value.toString())
				.forward(request, response);
		} catch (ServletException ex)
		{
			throw new UncheckedIOException(new IOException(ex));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
