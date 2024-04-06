package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

@ApplicationScoped
public abstract class HttpExceptionCatcher implements Catcher
{

	private final int status;

	public HttpExceptionCatcher(int status)
	{
		this.status = status;
	}

	@Override
	public void catches(HttpServletRequest request,
		HttpServletResponse response, Throwable exception)
	{
		response.setStatus(status);
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
