package gate.adapter.handler;

import gate.type.HttpError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;

@ApplicationScoped
public class HttpErrorHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			HttpError error = (HttpError) value;
			response.sendError(error.getCode(), error.getMessage());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}