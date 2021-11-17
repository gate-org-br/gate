package gate.handler;

import gate.handler.Handler;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
