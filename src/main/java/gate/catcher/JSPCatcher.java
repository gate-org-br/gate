package gate.catcher;

import gate.base.Screen;
import gate.error.AppException;
import gate.command.JSP;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JSPCatcher implements Catcher
{

	@Override
	public void execute(HttpServletRequest request,
		HttpServletResponse response,
		AppException exception)
	{
		try
		{
			try
			{
				Screen screen = (Screen) request.getAttribute("screen");
				screen.setMessages(exception.getMessage());

				request.getRequestDispatcher(JSP.getPath(request.getParameter("MODULE"),
					request.getParameter("SCREEN"),
					request.getParameter("ACTION")))
					.forward(request, response);
			} catch (ServletException ex)
			{
				throw new IOException(ex);
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
