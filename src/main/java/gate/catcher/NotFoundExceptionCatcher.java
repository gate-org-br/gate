package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotFoundExceptionCatcher extends HttpExceptionCatcher
{

	public NotFoundExceptionCatcher()
	{
		super(404);
	}
}
