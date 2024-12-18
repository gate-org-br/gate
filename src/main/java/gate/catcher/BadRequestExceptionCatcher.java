package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class BadRequestExceptionCatcher extends HttpExceptionCatcher
{

	public BadRequestExceptionCatcher()
	{
		super(HttpServletResponse.SC_BAD_REQUEST);
	}

}
