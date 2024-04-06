package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppExceptionCatcher extends HttpExceptionCatcher
{

	public AppExceptionCatcher()
	{
		super(400);
	}
}
