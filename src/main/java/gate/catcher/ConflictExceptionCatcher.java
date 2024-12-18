package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ConflictExceptionCatcher extends HttpExceptionCatcher
{

	public ConflictExceptionCatcher()
	{
		super(HttpServletResponse.SC_CONFLICT);
	}

}
