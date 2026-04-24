package gate.handler;

import gate.error.AppError;
import gate.error.ConversionException;
import jakarta.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class FileHandler implements Handler
{

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		try
		{
			if (part == null)
				return null;

			File file = File.createTempFile("File", "." + part.getSubmittedFileName());
			try (InputStream inputStream = part.getInputStream();
			     OutputStream outputStream = new FileOutputStream(file))
			{
				inputStream.transferTo(outputStream);
				return file;
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex, "Error trying to convert uploaded file");
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			java.io.File file = (java.io.File) value;
			response.setContentLength((int) file.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format(
				"attachment; filename=\"%s\"", URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)));
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
