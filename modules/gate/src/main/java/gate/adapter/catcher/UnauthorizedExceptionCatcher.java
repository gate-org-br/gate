package gate.adapter.catcher;

import gate.http.CookieAuthentication;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.UncheckedIOException;

@ApplicationScoped
public class UnauthorizedExceptionCatcher implements Catcher
{
	@Inject
	HttpExceptionCatcher catcher;

	@Override
	public void catches(ScreenServletRequest request,
	                    ScreenServletResponse response, Throwable exception)
	{
		if (request.getAuthentication() instanceof CookieAuthentication)
			response.revokeSessionCookie(request);

		if ("navigate".equalsIgnoreCase(request.getHeader("Sec-Fetch-Mode")))
		{
			try
			{
				response.sendRedirect("Gate");
				return;
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}

		catcher.catches(request, response, exception);
	}
}