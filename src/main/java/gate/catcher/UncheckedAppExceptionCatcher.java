package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UncheckedAppExceptionCatcher extends HttpExceptionCatcher
{

	public UncheckedAppExceptionCatcher()
	{
		super(400);
	}
}
