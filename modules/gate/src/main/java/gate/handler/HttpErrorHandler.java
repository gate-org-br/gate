package gate.handler;

import gate.error.AppError;
import gate.type.HttpError;
import java.io.IOException;
import java.io.UncheckedIOException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class HttpErrorHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
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
