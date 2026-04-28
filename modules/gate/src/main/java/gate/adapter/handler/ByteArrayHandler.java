package gate.adapter.handler;

import gate.error.ConversionException;
import jakarta.servlet.http.Part;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayHandler implements Handler
{
	@Override
	public void handle(jakarta.servlet.http.HttpServletRequest request,
	                   jakarta.servlet.http.HttpServletResponse response, Object value)
	{
		throw new UnsupportedOperationException("Byte arrays can't be handled as a servlet response.");
	}

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		try (BufferedInputStream stream = new BufferedInputStream(part.getInputStream());
		     ByteArrayOutputStream bytes = new ByteArrayOutputStream())
		{
			for (int i = stream.read(); i != -1; i = stream.read())
				bytes.write(i);
			return bytes.toByteArray();
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
