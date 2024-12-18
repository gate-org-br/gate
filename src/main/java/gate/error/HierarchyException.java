package gate.error;

/**
 * Signals that an invalid hierarchy relation was found.
 */
public class HierarchyException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an HierarchyException with the specified detail message.
	 *
	 * @param message The detail message, which are saved for later retrieval by the getMessages() or getMessage()
	 * method
	 */
	public HierarchyException(String message)
	{
		super(message);
	}

}
