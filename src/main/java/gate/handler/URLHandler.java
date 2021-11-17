package gate.handler;

import gate.error.AppError;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class URLHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			response.sendRedirect(response.encodeRedirectURL(value.toString()));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
