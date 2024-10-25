package gate.handler;

import gate.error.AppError;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class PathHandler implements Handler
{

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
