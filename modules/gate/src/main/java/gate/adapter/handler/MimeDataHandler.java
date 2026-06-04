package gate.adapter.handler;

import gate.error.ConversionException;
import gate.io.ByteArrayReader;
import gate.lang.contentType.ContentType;
import gate.type.mime.MimeData;
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
public class MimeDataHandler implements Handler
{

	@Override
	public Object ofPart(Type type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		try (InputStream is = part.getInputStream())
		{
			byte[] bytes = ByteArrayReader.getInstance().read(is);
			return MimeData.of(ContentType.valueOf(part.getContentType()), bytes);
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
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