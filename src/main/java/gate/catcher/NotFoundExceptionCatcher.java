package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class NotFoundExceptionCatcher extends HttpExceptionCatcher
{

	public NotFoundExceptionCatcher()
	{
		super(HttpServletResponse.SC_NOT_FOUND);
	}

}
