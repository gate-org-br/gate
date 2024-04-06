package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceUnavailableExceptionCatcher extends HttpExceptionCatcher
{

	public ServiceUnavailableExceptionCatcher()
	{
		super(503);
	}
}
