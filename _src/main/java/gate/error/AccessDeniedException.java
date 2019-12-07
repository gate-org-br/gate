package gate.error;

/**
 * Signals that the current user has no access to a resource.
 */
public class AccessDeniedException extends Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new AccessDeniedException.
	 */
	public AccessDeniedException()
	{
		super("Acesso negado");
	}
}
