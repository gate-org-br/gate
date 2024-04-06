package gate.handler;

import gate.error.AppError;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class FileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			java.io.File file = (java.io.File) value;
			response.setContentLength((int) file.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format(
				"attachment; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
			try ( BufferedInputStream is = new BufferedInputStream(
				new FileInputStream(file)))
			{
				try ( BufferedOutputStream os = new BufferedOutputStream(
					response.getOutputStream()))
				{
					for (int data = is.read(); data != -1; data = is.read())
						os.write(data);
				}
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
