package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class InternalServerExceptionCatcher extends HttpExceptionCatcher
{

	public InternalServerExceptionCatcher()
	{
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

}
