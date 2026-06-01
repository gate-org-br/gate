package gate.adapter.handler;

import gate.lang.contentType.ContentType;
import gate.lang.json.JsonElement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class EntityHandler implements Handler
{
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		response.setContentType(ContentType.APPLICATION_JSON.toString());
		try (OutputStream os = response.getOutputStream())
		{
			os.write(JsonElement.encode(value).toString().getBytes(StandardCharsets.UTF_8));
			os.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}