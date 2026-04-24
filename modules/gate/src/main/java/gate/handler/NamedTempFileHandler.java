package gate.handler;

import gate.type.NamedTempFile;
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
public class NamedTempFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{

		try ( NamedTempFile namedTempFile = (NamedTempFile) value)
		{

			response.setContentLength((int) namedTempFile.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"", URLEncoder.encode(namedTempFile.getName(), StandardCharsets.UTF_8)));

			try ( InputStream inputStream = namedTempFile.getInputStream();
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
