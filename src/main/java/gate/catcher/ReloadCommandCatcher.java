package gate.catcher;

import gate.catcher.Catcher;
import gate.error.AppException;
import gate.io.URL;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReloadCommandCatcher implements Catcher
{

	@Override
	public void execute(HttpServletRequest request,
		HttpServletResponse response,
		AppException exception)
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
