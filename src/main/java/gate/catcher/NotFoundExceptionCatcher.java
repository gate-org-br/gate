package gate.catcher;

public class NotFoundExceptionCatcher extends HttpExceptionCatcher
{

	public NotFoundExceptionCatcher()
	{
		super(404);
	}
}
