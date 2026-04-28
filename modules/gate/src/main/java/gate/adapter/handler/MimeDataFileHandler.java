package gate.adapter.handler;

import gate.error.AppError;
import gate.error.ConversionException;
import gate.io.ByteArrayReader;
import gate.lang.contentType.ContentType;
import gate.type.mime.MimeDataFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@ApplicationScoped
public class MimeDataFileHandler implements Handler
{

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		try
		{
			try (InputStream is = part.getInputStream())
			{
				byte[] bytes = ByteArrayReader.getInstance().read(is);
				return MimeDataFile.of(ContentType.valueOf(part.getContentType()),
						bytes, part.getSubmittedFileName());
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Erro ao obter arquivo", ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		MimeDataFile mimeDataFile = (MimeDataFile) value;

		response.setContentLength(mimeDataFile.getData().length);

		response.setContentType(mimeDataFile.getContentType().toString());

		response.setHeader("Content-Disposition",
			String.format("attachment; filename=\"%s\"",
				mimeDataFile.getName()));

		try ( OutputStream os = response.getOutputStream())
		{
			os.write(mimeDataFile.getData());
			os.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
