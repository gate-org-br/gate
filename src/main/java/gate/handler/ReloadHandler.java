package gate.handler;

import gate.io.URL;
import gate.command.Reload;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReloadHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			Reload command = (Reload) value;
			URL referer = URL.parse(request.getHeader("referer"));
			referer.setMessages(command.getMessages());
			response.sendRedirect(referer.toString());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
