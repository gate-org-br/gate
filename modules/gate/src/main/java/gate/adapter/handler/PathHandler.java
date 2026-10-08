package gate.adapter.handler;

import gate.error.AppError;
import gate.error.ConversionException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class PathHandler implements Handler
{

	@Override
	public Object ofPart(Type type, Part part) throws ConversionException
	{
		try
		{
			if (part == null)
				return null;

			String storage = CDI.current()
					.select(ServletContext.class)
					.get().getInitParameter("storage");

			File file = File.createTempFile("File",
					"." + part.getSubmittedFileName(),
					storage != null ? new File(storage) : null);

			try (InputStream inputStream = part.getInputStream();
			     OutputStream outputStream = new FileOutputStream(file))
			{
				inputStream.transferTo(outputStream);
				return file.toPath();
			}
		} catch (IOException ex)
		{
			throw new ConversionException("Error trying to convert uploaded file", ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			Path path = (Path) value;

			response.setContentLength((int) Files.size(path));
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
					URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8)));
			try (BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(path.toFile())))
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
		}
	}
}