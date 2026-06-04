package gate.adapter.handler;

import gate.Progress;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;

@ApplicationScoped
public class URLHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			response.sendRedirect(response.encodeRedirectURL(value.toString()));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request,
	                   Progress progress, Object value)
	{
		progress.result("text/plain",
				null, value.toString());
	}
}