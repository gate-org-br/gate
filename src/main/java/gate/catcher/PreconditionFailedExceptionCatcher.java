package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class PreconditionFailedExceptionCatcher extends HttpExceptionCatcher
{

	public PreconditionFailedExceptionCatcher()
	{
		super(HttpServletResponse.SC_PRECONDITION_FAILED);
	}

}
