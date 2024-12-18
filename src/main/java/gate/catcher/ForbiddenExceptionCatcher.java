package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ForbiddenExceptionCatcher extends HttpExceptionCatcher
{

	public ForbiddenExceptionCatcher()
	{
		super(HttpServletResponse.SC_FORBIDDEN);
	}

}
