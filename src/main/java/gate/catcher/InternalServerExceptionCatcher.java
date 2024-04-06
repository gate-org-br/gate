package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InternalServerExceptionCatcher extends HttpExceptionCatcher
{

	public InternalServerExceptionCatcher()
	{
		super(500);
	}
}
