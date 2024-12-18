package gate.error;

import java.util.List;

public abstract class HttpException extends AppException
{

	public HttpException(String message)
	{
		super(message);
	}

	public HttpException(List<String> messages)
	{
		super(messages);
	}

	public HttpException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public HttpException(List<String> messages, Throwable cause)
	{
		super(messages, cause);
	}

	public abstract int getStatusCode();
}
