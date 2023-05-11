package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppExceptionCatcher extends HttpExceptionCatcher
{

	public AppExceptionCatcher()
	{
		super(400);
	}
}
