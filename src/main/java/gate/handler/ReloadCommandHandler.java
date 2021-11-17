package gate.handler;

import gate.io.URL;
import gate.command.ReloadCommand;
import gate.handler.Handler;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ReloadCommandHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			ReloadCommand command = (ReloadCommand) value;
			URL referer = URL.parse(request.getHeader("referer"));
			referer.setMessages(command.getMessages());
			response.sendRedirect(referer.toString());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
