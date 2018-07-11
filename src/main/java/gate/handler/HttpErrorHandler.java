package gate.handler;

import gate.error.AppError;
import gate.type.HttpError;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpErrorHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			HttpError error = (HttpError) value;
			response.sendError(error.getCode(), error.getMessage());
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}
}
