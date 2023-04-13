package gate.catcher;

public class ServiceUnavailableExceptionCatcher extends HttpExceptionCatcher
{

	public ServiceUnavailableExceptionCatcher()
	{
		super(503);
	}
}
