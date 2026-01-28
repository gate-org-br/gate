package gate.error;

public class MethodNotAllowedException extends HttpException
{

	public MethodNotAllowedException()
	{
		super("Method Not Allowed");
	}

	@Override
	public int getStatusCode()
	{
		return 405;
	}

}
