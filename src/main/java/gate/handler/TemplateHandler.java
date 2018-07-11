package gate.handler;

import gate.error.AppError;
import java.io.IOException;
import java.io.Writer;
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
				request.getRequestDispatcher(string)
						.forward(request, response);
			} catch (ServletException | IOException e)
			{
				throw new AppError(e);
			}
		} else
		{
			response.setContentType("text/plain");
			try (Writer writer = response.getWriter())
			{
				writer.write(string);
				writer.flush();
			} catch (Exception e)
			{
				throw new AppError(e);
			}
		}
	}

}
