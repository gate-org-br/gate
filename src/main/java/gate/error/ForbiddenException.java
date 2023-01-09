package gate.error;

/**
 * Signals that the current user has no access to a resource.
 */
public class ForbiddenException extends AppException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new ForbiddenException.
	 */
	public ForbiddenException()
	{
		super("Acesso negado");
	}
}
