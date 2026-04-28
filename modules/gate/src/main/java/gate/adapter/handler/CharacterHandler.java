package gate.adapter.handler;

import gate.error.ConversionException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class CharacterHandler implements Handler
{

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		try (var is = part.getInputStream())
		{
			String value = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return value.isEmpty() ? null : value.charAt(0);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value != null ? value.toString() : "";
		response.setContentType("text/plain");
		try (Writer writer = response.getWriter())
		{
			writer.write(string);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
