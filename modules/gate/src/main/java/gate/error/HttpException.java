package gate.error;

import gate.adapter.catcher.HttpExceptionCatcher;
import gate.annotation.Catcher;

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