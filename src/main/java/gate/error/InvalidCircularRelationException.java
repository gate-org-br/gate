package gate.error;

/**
 * Signals that an invalid circular relation was found.
 */
public class InvalidCircularRelationException extends AppException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InvalidCircularRelationException with the default message.
	 */
	public InvalidCircularRelationException()
	{
		super("Tentativa de criar relação circular inválida");
	}

	/**
	 * Constructs an InvalidCircularRelationException with the specified detail message.
	 *
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage()
	 * method
	 */
	public InvalidCircularRelationException(String message)
	{
		super(message);
	}

}
