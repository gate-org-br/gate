package gate.handler;

import gate.error.AppError;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IntegerHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response, Object value) throws AppError
	{
		Integer integer = (Integer) value;
		String string = integer != null ? integer.toString() : "";
		response.setContentType("text/plain");
		response.setContentLength(string.length());

		try (Writer writer = response.getWriter())
		{
			writer.write(string);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
