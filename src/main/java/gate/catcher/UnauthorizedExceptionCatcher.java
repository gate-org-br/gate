package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class UnauthorizedExceptionCatcher extends HttpExceptionCatcher
{

	public UnauthorizedExceptionCatcher()
	{
		super(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
