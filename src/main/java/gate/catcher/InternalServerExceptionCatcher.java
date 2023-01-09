package gate.catcher;

public class InternalServerExceptionCatcher extends HttpExceptionCatcher
{

	public InternalServerExceptionCatcher()
	{
		super(500);
	}
}
