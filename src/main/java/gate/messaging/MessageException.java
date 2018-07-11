package gate.messaging;

import gate.error.AppException;

public class MessageException extends AppException
{

    public MessageException(String message)
    {
	super(message);
    }

    public MessageException(String message, Object... parameters)
    {
	super(message, parameters);
    }

    public MessageException(Throwable cause,
	    String message)
    {
	super(cause, message);
    }

    public MessageException(Throwable cause,
	    String message, Object... parameters)
    {
	super(cause, message, parameters);
    }

}
