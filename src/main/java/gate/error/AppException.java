package gate.error;

import gate.annotation.Catcher;
import gate.catcher.AppExceptionCatcher;
import gate.converter.Converter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Signals that an application level exception of some sort has occurred.
 * <p>
 * This class is the general class of application level exceptions produced by user actions.
 */
@Catcher(AppExceptionCatcher.class)
public class AppException extends Exception
{

	private final List<String> messages;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an AppException with the specified detail message.
	 *
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage() method
	 */
	public AppException(String message)
	{
		Objects.requireNonNull(message);
		this.messages = Collections.singletonList(message);
	}

	/**
	 * Constructs an AppException with the specified detail messages.
	 *
	 * @param messages The detail messages, which are saved for later retrieval by the getMessages() method
	 */
	public AppException(List<String> messages)
	{
		Objects.requireNonNull(messages);
		this.messages = Collections.unmodifiableList(messages);
	}

	/**
	 * Constructs an AppException with the specified cause and detail message.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted, and indicates that the cause is
	 * nonexistent or unknown.
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage() method
	 */
	public AppException(String message, Throwable cause)
	{
		super(cause);
		Objects.requireNonNull(message);
		this.messages = Collections.singletonList(message);
	}

	/**
	 * Constructs an AppException with the specified cause and detail messages.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted, and indicates that the cause is
	 * nonexistent or unknown.
	 * @param messages The detail messages, which are saved for later retrieval by the getMessages() method
	 */
	public AppException(List<String> messages, Throwable cause)
	{
		super(cause);
		Objects.requireNonNull(messages);
		this.messages = Collections.unmodifiableList(messages);
	}

	/**
	 * Returns the detail messages of this exception as a String.
	 *
	 * @return a String containing all detail messages of this exception
	 */
	@Override
	public String getMessage()
	{
		return Converter.toText(getMessages());
	}

	/**
	 * Returns the detail messages of this exception.
	 *
	 * @return a list containing all detail messages of this exception
	 */
	public List<String> getMessages()
	{
		return messages;
	}
}
