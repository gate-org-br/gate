package gate.handler;

import gate.error.AppError;
import gate.error.ConversionException;
import gate.type.PNG;
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.io.Writer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class PNGHandler implements Handler
{

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		if (part == null
		    || part.getSubmittedFileName().isEmpty())
			return null;

		try
		{
			try (InputStream is = part.getInputStream())
			{
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
				{
					for (int c = is.read(); c != -1; c = is.read())
						baos.write(c);
					return new PNG(baos.toByteArray());
				}
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Erro ao obter arquivo", ex);
		}

	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		String string = value.toString();
		response.setContentType("image/png");
		response.setContentLength(string.length());
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
