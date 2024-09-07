package gate.handler;

import gate.Progress;
import gate.error.AppError;
import gate.lang.json.JsonElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class JsonElementHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		String string = JsonElement.format((JsonElement) value);
		response.setContentType("application/json");

		try (Writer writer = response.getWriter())
		{
			writer.write(string);
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			Progress progress, Object value)
	{
		String string = JsonElement.format((JsonElement) value);
		progress.result("application/json", null, string);
	}
}
