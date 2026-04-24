package gate.messaging;

import gate.error.AppException;

/**
 * Exception thrown for messaging-specific failures.
 *
 * <p>This exception is used by the public messaging API to report invalid usage, missing configuration and transport
 * errors when sending or processing mail.
 */
public class MessageException extends AppException
{

	/**
	 * Creates a messaging exception with the given message.
	 *
	 * @param message the exception message
	 */
	public MessageException(String message)
	{
		super(message);
	}

	/**
	 * Creates a messaging exception with the given message and cause.
	 *
	 * @param message the exception message
	 * @param cause the underlying cause
	 */
	public MessageException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
