package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;

@ApplicationScoped
public class UnauthorizedExceptionCatcher extends HttpExceptionCatcher
{

	@Override
	public void catches(HttpServletRequest request,
						HttpServletResponse response, Throwable exception)
	{
		if ("navigate".equalsIgnoreCase(request.getHeader("Sec-Fetch-Mode")))
		{
			try
			{
				response.sendRedirect("Gate");
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		} else
			super.catches(request, response, exception);
	}
}