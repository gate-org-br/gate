package gate.catcher;

import gate.error.HttpException;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

@ApplicationScoped
public class HttpExceptionCatcher implements Catcher
{

	@Override
	public void catches(ScreenServletRequest request,
	                    ScreenServletResponse response, Throwable exception)
	{

		response.setStatus(((HttpException) exception).getStatusCode());
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