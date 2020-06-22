package gate.error;

import java.io.IOException;

/**
 * Signals that a data conversion could not be made.
 */
public class ConversionException extends IOException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a ConversionException with the specified detail message.
	 *
	 * @param message The detail message, which is saved for later retrieval by the getMessage() method
	 */
	public ConversionException(String message)
	{
		super(message);
	}

	/**
	 * Constructs an ConversionException with the specified detail message formatted with the specified parameters.
	 *
	 * @param message The detail message, which is saved for later retrieval by the getMessage() method
	 * @param parameters parameters to formatted into the detail message
	 *
	 * @see java.util.Formatter
	 */
	public ConversionException(String message, Object... parameters)
	{
		this(String.format(message, parameters));
	}

	/**
	 * Constructs an ConversionException with the specified cause and detail message.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is
	 * permitted, and indicates that the cause is nonexistent or unknown.
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage()
	 * method
	 */
	public ConversionException(Throwable cause, String message)
	{
		super(message, cause);
	}

	/**
	 * Constructs an ConversionException with the specified detail message formatted with the specified parameters.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is
	 * permitted, and indicates that the cause is nonexistent or unknown.
	 * @param message The detail message, which is saved for later retrieval by the getMessage() method
	 * @param parameters parameters to formatted into the detail message
	 *
	 * @see java.util.Formatter
	 */
	public ConversionException(Throwable cause, String message, Object... parameters)
	{
		this(cause, String.format(message, parameters));
	}

}
