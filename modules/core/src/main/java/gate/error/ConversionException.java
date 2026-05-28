package gate.error;

/**
 * Signals that a data conversion could not be made.
 */
public class ConversionException extends RuntimeException
{
	/**
	 * Constructs a ConversionException with the specified detail message.
	 *
	 * @param message The detail message, which is saved for later retrieval
	 *                by the getMessage() method
	 */
	public ConversionException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a ConversionException with the specified cause and detail
	 * message.
	 *
	 * @param message The detail message, which are saved for later
	 *                retrieval by the getMessages() or getMessage() method
	 * @param cause   The cause, which is saved for later retrieval by the
	 *                getCause() method. A null value is permitted and indicates that the
	 *                cause is nonexistent or unknown.
	 */
	public ConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}