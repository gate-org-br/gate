package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InternalServerExceptionCatcher extends HttpExceptionCatcher
{

	public InternalServerExceptionCatcher()
	{
		super(500);
	}
}
