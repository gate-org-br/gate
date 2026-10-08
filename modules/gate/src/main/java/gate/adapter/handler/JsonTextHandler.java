package gate.adapter.handler;

import gate.Progress;
import gate.adapter.jsonRenderer.JsonRenderer;
import gate.lang.contentType.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class JsonTextHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			String string = JsonRenderer.render(value).toString();
			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

			response.setCharacterEncoding("UTF-8");
			response.setContentLength(bytes.length);
			response.setContentType("application/json");

			try (OutputStream os = response.getOutputStream())
			{
				os.write(bytes);
				os.flush();
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, Progress progress, Object value)
	{
		progress.result(ContentType.APPLICATION_JSON.toString(),
				null,
				JsonRenderer.render(value).toString());
	}
}