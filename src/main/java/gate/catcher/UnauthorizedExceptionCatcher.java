package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnauthorizedExceptionCatcher extends HttpExceptionCatcher
{

	public UnauthorizedExceptionCatcher()
	{
		super(401);
	}
}
