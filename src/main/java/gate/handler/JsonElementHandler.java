package gate.handler;

import gate.error.AppError;
import gate.lang.json.JsonElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class JsonElementHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		String string = JsonElement.format((JsonElement) value);
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
