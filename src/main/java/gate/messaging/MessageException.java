package gate.messaging;

import gate.error.AppException;

public class MessageException extends AppException
{

	public MessageException(String message)
	{
		super(message);
	}

	public MessageException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
