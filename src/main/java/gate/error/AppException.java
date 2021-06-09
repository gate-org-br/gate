package gate.error;

import gate.annotation.Handler;
import gate.converter.Converter;
import gate.handler.AppExceptionHandler;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Signals that an application level exception of some sort has occurred.
 * <p>
 * This class is the general class of application level exceptions produced by user actions.
 */
@Handler(AppExceptionHandler.class)
public class AppException extends Exception
{

	private final List<String> messages;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an AppException with the specified cause and detail messages.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted,
	 * and indicates that the cause is nonexistent or unknown.
	 * @param messages The detail messages, which are saved for later retrieval by the getMessages() method
	 */
	public AppException(Throwable cause, List<String> messages)
	{
		super(cause);
		Objects.requireNonNull(messages);
		this.messages = Collections.unmodifiableList(messages);
	}

	/**
	 * Constructs an AppException with the specified cause and detail message.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted,
	 * and indicates that the cause is nonexistent or unknown.
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage()
	 * method
	 */
	public AppException(Throwable cause, String message)
	{
		super(cause);
		Objects.requireNonNull(message);
		this.messages = Collections.singletonList(message);
	}

	/**
	 * Constructs an AppException with the specified detail message formatted with the specified parameters.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted,
	 * and indicates that the cause is nonexistent or unknown.
	 * @param message The detail message, which is saved for later retrieval by the getMessages() or getMessage() method
	 * @param parameters parameters to formatted into the detail message
	 *
	 * @see java.util.Formatter
	 */
	public AppException(Throwable cause, String message, Object... parameters)
	{
		super(cause);
		Objects.requireNonNull(message);
		this.messages = Collections.singletonList(String.format(message, parameters));
	}

	/**
	 * Constructs an AppException with the specified detail message formatted with the specified parameters.
	 *
	 * @param message The detail message, which is saved for later retrieval by the getMessages() or getMessage() method
	 * @param parameters parameters to formatted into the detail message
	 *
	 * @see java.util.Formatter
	 */
	public AppException(String message, Object... parameters)
	{
		Objects.requireNonNull(message);
		this.messages = Collections.singletonList(String.format(message, parameters));
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
	 * Constructs an AppException with the specified detail message.
	 *
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage()
	 * method
	 */
	public AppException(String message)
	{
		Objects.requireNonNull(message);
		this.messages = Collections.singletonList(message);
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
	 * Throws an AppException with the specified message if the specified object is null.
	 *
	 * 
	 * @param object object to be tested for null
	 * @param message message of the AppException to be generated if the specified object is null
	 *
	 * @return the same object passed as the first parameter
	 *
	 * @throws gate.error.AppException if the specified object is null
	 */
	public static <T> T require(T object, String message) throws AppException
	{
		if (object == null)
			throw new AppException(message);
		return object;
	}

	/**
	 * Throws an AppException with the specified message if the specified assertion is false.
	 *
	 * @param assertion the assertion to be checked
	 * @param message message of the AppException to be generated if the specified assertion is false
	 *
	 * @throws gate.error.AppException if the specified assertion is false
	 */
	public static void assertTrue(boolean assertion, String message) throws AppException
	{
		if (!assertion)
			throw new AppException(message);
	}

	/**
	 * Throws an AppException with the specified message if the specified assertion is true.
	 *
	 * @param assertion the assertion to be checked
	 * @param message message of the AppException to be generated if the specified assertion is true
	 *
	 * @throws gate.error.AppException if the specified assertion is true
	 */
	public static void assertFalse(boolean assertion, String message) throws AppException
	{
		if (assertion)
			throw new AppException(message);
	}
}
