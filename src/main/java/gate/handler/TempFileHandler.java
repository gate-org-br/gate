package gate.handler;

import gate.io.IOStreamTransferer;
import gate.type.TempFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TempFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{

		try (TempFile tempFile = (TempFile) value)
		{

			response.setContentLength((int) tempFile.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"file.dat\"");

			try (InputStream inputStream = tempFile.getInputStream();
				OutputStream outputStream = response.getOutputStream())
			{
				IOStreamTransferer.transfer(inputStream, outputStream);
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
