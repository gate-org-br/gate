package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceUnavailableExceptionCatcher extends HttpExceptionCatcher
{

	public ServiceUnavailableExceptionCatcher()
	{
		super(503);
	}
}
