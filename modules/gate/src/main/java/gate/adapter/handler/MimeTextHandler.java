package gate.adapter.handler;

import gate.error.ConversionException;
import gate.io.ByteArrayReader;
import gate.lang.contentType.ContentType;
import gate.type.mime.MimeText;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;

@ApplicationScoped
public class MimeTextHandler implements Handler
{

	@Override
	public Object ofPart(Type type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		try
		{
			try (InputStream is = part.getInputStream())
			{
				byte[] bytes = ByteArrayReader.getInstance().read(is);
				return MimeText.of(ContentType.valueOf(part.getContentType()),
						"utf-8",
						bytes);
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Erro ao obter arquivo", ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value.toString();
		response.setContentType("text/plain");

		try (Writer writer = response.getWriter())
		{
			writer.write(string);
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}