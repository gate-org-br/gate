package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BatdRequestExceptionCatcher extends HttpExceptionCatcher
{

	public BatdRequestExceptionCatcher()
	{
		super(400);
	}
}
