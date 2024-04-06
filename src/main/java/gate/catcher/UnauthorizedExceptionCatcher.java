package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnauthorizedExceptionCatcher extends HttpExceptionCatcher
{

	public UnauthorizedExceptionCatcher()
	{
		super(401);
	}
}
