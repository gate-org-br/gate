package gate.error;

public abstract class HttpException extends RuntimeException
{

	public HttpException(String message)
	{
		super(message);
	}

	public HttpException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public abstract int getStatusCode();
}