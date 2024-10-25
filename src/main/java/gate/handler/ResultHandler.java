package gate.handler;

import gate.error.AppError;
import gate.type.Result;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ResultHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		String string = value.toString();
		response.setContentType("application/json");

		try ( Writer writer = response.getWriter())
		{
			writer.write(string);
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
