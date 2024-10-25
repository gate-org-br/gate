package gate.handler;

import gate.converter.Converter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class JsonTextHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			String string = Converter.toJsonText(value);
			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

			response.setCharacterEncoding("UTF-8");
			response.setContentLength(bytes.length);
			response.setContentType("application/json");

			try ( OutputStream os = response.getOutputStream())
			{
				os.write(bytes);
				os.flush();
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
