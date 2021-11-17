package gate.handler;

import gate.error.AppError;
import gate.type.HttpError;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

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
