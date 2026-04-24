package gate.handler;

import gate.error.ConversionException;
import gate.type.TempFile;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class TempFileHandler implements Handler
{

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		if (part == null)
			return null;

		TempFile tempFile = TempFile.empty()
				.named(part.getSubmittedFileName());

		try (InputStream inputStream = part.getInputStream();
		     OutputStream outputStream = tempFile.getOutputStream())
		{
			inputStream.transferTo(outputStream);
			return tempFile;
		} catch (IOException ex)
		{
			tempFile.close();
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{

		try ( TempFile tempFile = (TempFile) value)
		{

			response.setContentLength((int) tempFile.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"file.dat\"");

			try ( InputStream inputStream = tempFile.getInputStream();
				 OutputStream outputStream = response.getOutputStream())
			{
				inputStream.transferTo(outputStream);
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
