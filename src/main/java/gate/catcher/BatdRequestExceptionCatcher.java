package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BatdRequestExceptionCatcher extends HttpExceptionCatcher
{

	public BatdRequestExceptionCatcher()
	{
		super(400);
	}
}
