package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotFoundExceptionCatcher extends HttpExceptionCatcher
{

	public NotFoundExceptionCatcher()
	{
		super(404);
	}
}
