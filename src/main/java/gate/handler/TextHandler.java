package gate.handler;

import gate.error.AppError;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
					   HttpServletResponse response, Object value) throws AppError
	{
		String string = value.toString();
		response.setContentType("text/plain");

		try (Writer writer = response.getWriter())
		{
			writer.write(string);
			writer.flush();
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
