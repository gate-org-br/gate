package gate.handler;

import gate.type.Form;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class FormHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			String string = ((Form) value).toJson().toString();
			byte[] bytes = string.getBytes(Charset.forName("UTF-8"));

			response.setCharacterEncoding("UTF-8");
			response.setContentLength(bytes.length);
			response.setContentType("application/json");

			try ( OutputStream os = response.getOutputStream())
			{
				os.write(bytes);
				os.flush();
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
