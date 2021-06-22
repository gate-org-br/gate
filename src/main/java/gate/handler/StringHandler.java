package gate.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StringHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response, Object value)
	{
		String string = value.toString();
		if (string.endsWith(".jsp"))
			JSPTemplateHandler.handleTemplate(request, response, string);
		else if (string.endsWith(".html"))
			HTMLTemplateHandler.handleTemplate(request, response, string);
		else
		{
			response.setContentType("text/plain");
			try (Writer writer = response.getWriter())
			{
				writer.write(value.toString());
				writer.flush();
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}
	}
}
