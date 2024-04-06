package gate.error;

public abstract class HttpException extends AppException
{

	public HttpException(String message)
	{
		super(message);
	}

	public abstract int getStatusCode();
}
