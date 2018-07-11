package gate.handler;

import gate.error.AppError;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EnumHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
			   HttpServletResponse response, Object object) throws AppError
	{
		Enum<?> value = (Enum<?>) object;
		String string = value != null ? String.valueOf(value.ordinal()) : "";
		response.setContentType("text/plain");
		response.setContentLength(string.length());

		try (Writer writer = response.getWriter())
		{
			writer.write(string);
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
