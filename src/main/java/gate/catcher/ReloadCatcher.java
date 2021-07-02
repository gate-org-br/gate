package gate.catcher;

import gate.io.URL;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReloadCatcher implements Catcher
{

	@Override
	public void execute(HttpServletRequest request,
		HttpServletResponse response,
		Throwable exception)
	{
		try
		{
			URL referer = URL.parse(request.getHeader("referer"));
			referer.setMessages(exception.getMessage());
			response.sendRedirect(referer.toString());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
