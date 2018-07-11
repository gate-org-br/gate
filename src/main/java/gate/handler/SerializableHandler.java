package gate.handler;

import gate.error.AppError;
import gate.converter.Converter;

import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SerializableHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
					   HttpServletResponse response, Object value) throws AppError
	{
		String string = Converter.toString(value);
		byte[] bytes = string.getBytes(Charset.forName("UTF-8"));

		response.setCharacterEncoding("UTF-8");
		response.setContentLength(bytes.length);
		response.setContentType("application/java-serialized-object");

		try (OutputStream os = response.getOutputStream())
		{
			os.write(bytes);
			os.flush();
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
