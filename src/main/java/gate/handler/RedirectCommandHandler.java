package gate.handler;

import gate.Progress;
import gate.error.AppError;
import java.io.IOException;
import java.io.UncheckedIOException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class RedirectCommandHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			response.sendRedirect(value.toString());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		Progress progress, Object value) throws AppError
	{
		progress.result("text/plain",
			null, value.toString());
	}
}
