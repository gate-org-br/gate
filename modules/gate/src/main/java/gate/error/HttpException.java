package gate.error;

import gate.annotation.Catcher;
import gate.catcher.HttpExceptionCatcher;

@Catcher(HttpExceptionCatcher.class)
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

	public HttpException(Throwable cause)
	{
		super(cause);
	}

	public abstract int getStatusCode();
}