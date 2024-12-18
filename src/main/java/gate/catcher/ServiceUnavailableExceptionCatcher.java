package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ServiceUnavailableExceptionCatcher extends HttpExceptionCatcher
{

	public ServiceUnavailableExceptionCatcher()
	{
		super(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
	}

}
