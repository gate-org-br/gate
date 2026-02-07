package gate.handler;

import gate.command.ReloadCommand;
import gate.io.URL;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;

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
