package gate.catcher;

public class UnauthorizedExceptionCatcher extends HttpExceptionCatcher
{

	public UnauthorizedExceptionCatcher()
	{
		super(401);
	}
}
