package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UncheckedAppExceptionCatcher extends HttpExceptionCatcher
{

	public UncheckedAppExceptionCatcher()
	{
		super(400);
	}
}
