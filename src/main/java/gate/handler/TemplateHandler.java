package gate.handler;

import gate.error.AppError;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response, Object value)
		throws AppError
	{
		String string = value.toString();
		if (string.endsWith(".jsp"))
		{
			try
			{
				try
				{
					request.getRequestDispatcher(string)
						.forward(request, response);
				} catch (ServletException ex)
				{
					throw new IOException(ex);
				}
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		} else
		{
			response.setContentType("text/plain");
			try (Writer writer = response.getWriter())
			{
				writer.write(string);
				writer.flush();
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}
	}

}
