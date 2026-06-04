package gate.adapter.handler;


import gate.error.ConversionException;
import gate.type.DataFile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.lang.reflect.Type;

@ApplicationScoped
public class DataFileHandler implements Handler
{

	@Override
	public Object ofPart(Type type, Part part) throws ConversionException
	{
		if (part == null
				|| part.getSubmittedFileName().isEmpty())
			return null;

		try (InputStream is = part.getInputStream())
		{
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				for (int c = is.read(); c != -1; c = is.read())
					baos.write(c);
				return DataFile.of(baos.toByteArray(), part.getSubmittedFileName());
			}
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		DataFile dataFile = (DataFile) value;

		response.setContentType("application/octet-stream");
		response.setContentLength(dataFile.getData().length);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", dataFile.getName()));

		try (OutputStream os = response.getOutputStream())
		{
			os.write(dataFile.getData());
			os.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}