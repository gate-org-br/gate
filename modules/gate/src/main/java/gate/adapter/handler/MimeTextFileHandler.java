package gate.adapter.handler;

import gate.error.ConversionException;
import gate.io.ByteArrayReader;
import gate.lang.contentType.ContentType;
import gate.type.mime.MimeTextFile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

@ApplicationScoped
public class MimeTextFileHandler implements Handler
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
				return MimeTextFile.of(ContentType.valueOf(part.getContentType()),
						"utf-8",
						bytes,
						part.getSubmittedFileName());
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Erro ao obter arquivo", ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			MimeTextFile mimeTextFile = (MimeTextFile) value;
			response.setCharacterEncoding(mimeTextFile.getCharset());

			response.setContentType(String.format("%s/%s; charset=%s",
					mimeTextFile.getContentType().getType(),
					mimeTextFile.getContentType().getSubtype(),
					mimeTextFile.getCharset()));

			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"",
							mimeTextFile.getName()));

			byte[] bytes = mimeTextFile.getText().getBytes(mimeTextFile.getCharset());
			response.setContentLength(bytes.length);

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
}