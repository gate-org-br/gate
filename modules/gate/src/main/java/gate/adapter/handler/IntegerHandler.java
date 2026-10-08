package gate.adapter.handler;

import gate.adapter.converter.Converter;
import gate.error.ConversionException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class IntegerHandler implements Handler
{

	@Override
	public Object ofPart(Type type, Part part) throws ConversionException
	{
		try (var is = part.getInputStream())
		{
			return Converter.getConverter(type).ofString(type,
					new String(is.readAllBytes(), StandardCharsets.UTF_8));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
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