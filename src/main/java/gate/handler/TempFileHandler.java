package gate.handler;

import gate.type.TempFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TempFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response, Object value)
	{
		TempFile tempFile = (TempFile) value;
		try
		{

			response.setContentLength((int) tempFile.getFile().length());
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", String.format(
				"attachment; filename=\"%s\"", URLEncoder.encode(tempFile.getFile().getName(), "UTF-8")));
			try (BufferedInputStream is = new BufferedInputStream(
				new FileInputStream(tempFile.getFile())))
			{
				try (BufferedOutputStream os = new BufferedOutputStream(
					response.getOutputStream()))
				{
					for (int data = is.read(); data != -1; data = is.read())
						os.write(data);
				}
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		} finally
		{
			tempFile.delete();
		}
	}
}
