package gate.handler;

import gate.error.AppException;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppExceptionHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			AppException exception = (AppException) value;
			response.sendError(420, exception.getMessage());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
