package gate.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JSPHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response, Object value)
	{
		handleTemplate(request, response, value);
	}

	public static void handleTemplate(HttpServletRequest request,
		HttpServletResponse response, Object value)
	{
		try
		{
			try
			{
				request.getRequestDispatcher(value.toString())
					.forward(request, response);
			} catch (ServletException ex)
			{
				throw new IOException(ex);
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
