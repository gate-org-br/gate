package gate.catcher;

import gate.error.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

@ApplicationScoped
public class HttpExceptionCatcher implements Catcher
{

	@Override
	public void catches(HttpServletRequest request,
	                    HttpServletResponse response, Throwable exception)
	{

		HttpException httpException = (HttpException) exception;

		if (httpException.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED
		    && "navigate".equalsIgnoreCase(request.getHeader("Sec-Fetch-Mode")))
		{
			try
			{
				response.sendRedirect("Gate");
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}

		response.setStatus(httpException.getStatusCode());
		response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");

		try (Writer writer = response.getWriter())
		{
			writer.write(exception.getMessage());
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}